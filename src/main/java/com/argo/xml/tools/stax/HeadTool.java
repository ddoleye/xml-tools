package com.argo.xml.tools.stax;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Stack;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.argo.xml.tools.XMLTool;

/**
 *
 */
public class HeadTool extends StAXBase implements XMLTool {

	private int max = 10;
	private String path = null;

	public HeadTool() {
	}

	@Override
	public void configure(String[] args) {
		if (args == null)
			return;

		int length = args.length;
		if (args.length == 0)
			return;

		for (int index = 0; index < length; index++) {
			String arg = args[index];
			if (arg.matches("^\\-\\d+$")) {
				max = Integer.parseInt(arg.substring(1), 10);
			} else {
				path = arg;
			}
		}
	}

	@Override
	public void run(InputStream input, OutputStream output) throws IOException {
		if (path == null)
			throw new IllegalArgumentException("path");

		XMLEventReader reader = null;
		XMLEventWriter writer = null;
		try {
			reader = createReader(input);
			writer = createWriter(output, "UTF-8");
			extract(reader, writer, path, max);
		} catch (XMLStreamException e) {
			throw new IOException(e);
		} finally {
			closeQuietly(writer);
			closeQuietly(reader);
		}
	}

	private static class Element {
		XMLEvent event;
		boolean out;

		public Element(XMLEvent event) {
			this.event = event;
		}

		boolean isOut() {
			return out;
		}

		void setOut() {
			out = true;
		}
	}

	private void extract(XMLEventReader reader, XMLEventWriter writer,
			String path, int max) throws XMLStreamException {
		StringBuilder currentPath = new StringBuilder(1000);
		Stack<Element> elements = new Stack<Element>();
		int depth = 0;
		int count = 0;

		Characters newline = XMLEventFactory.newFactory()
				.createCharacters("\n");

		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();

			if (event.isStartDocument()) {
				// 시작/종료는 출력. 캡쳐된것이 없으면 빈 XML을 출력할 것이다.
				writer.add(event);
			} else if (event.isEndDocument()) {
				// 시작/종료는 출력
				writer.add(event);
			} else if (event.isStartElement()) {
				StartElement element = event.asStartElement();
				String name = element.getName().getLocalPart();
				currentPath.append("/").append(name);

				Element e = new Element(event);
				elements.add(e);

				if (depth > 0) {
					writer.add(e.event);
					e.setOut();
					depth++;
				} else {
					if (currentPath.toString().equals(path) && count < max) {
						depth++;
						count++;

						for (Element elm : elements) {
							if (!elm.isOut()) {
								writer.add(elm.event);
								elm.setOut();
							}
						}
					}
				}
			} else if (event.isEndElement()) {
				Element e = elements.pop();
				if (e.isOut()) {
					// 시작 태그가 출력되었다면 종료 태그도 출력한다.
					writer.add(event);
					if (depth == 1)
						writer.add(newline);
					writer.flush();
				}

				if (depth > 0) {
					depth--;
				}

				currentPath.setLength(currentPath.lastIndexOf("/"));
			} else {
				if (depth > 0) {
					writer.add(event);
				}
			}
		}
	}
}
