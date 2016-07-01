package com.argo.xml.tools.stax;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Stack;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.argo.xml.tools.XMLTool;

/**
 * 
 */
public class SimpleExtractTool extends StAXBase implements XMLTool {

	private StringBuilder buf = new StringBuilder();
	private String[] paths;

	public SimpleExtractTool() {
	}

	@Override
	public void configure(String[] args) {
		paths = args;
	}

	@Override
	public void run(InputStream input, OutputStream output) throws IOException {
		if (paths == null || paths.length == 0)
			throw new IllegalStateException();

		XMLEventReader reader = null;
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(output);
			reader = createReader(input);
			extract(paths, reader, writer);
		} catch (XMLStreamException e) {
			throw new IOException(e);
		} finally {
			closeQuietly(writer);
			closeQuietly(reader);
		}
	}

	private void extract(String[] paths, XMLEventReader reader,
			OutputStreamWriter writer) throws XMLStreamException, IOException {
		StringBuilder currentPath = new StringBuilder(1000);
		String currentPathStr = null;
		Stack<Integer> positions = new Stack<Integer>();
		int depth = 0;

		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			if (event.isStartElement()) {
				StartElement element = event.asStartElement();
				String name = element.getName().getLocalPart();

				positions.add(currentPath.length());
				currentPath.append("/").append(name);
				currentPathStr = currentPath.toString();
				if (depth > 0) {
					depth++;
				} else {
					for (String p : paths) {
						if (currentPathStr.equals(p)) {
							depth++;
							break;
						}
					}

					if (depth > 0) {
						writer.write(event.getLocation().getLineNumber()
								+ ":\t");
						writer.write(currentPathStr);
						writer.write("\t");
					}
				}

			} else if (event.isEndElement()) {
				if (depth > 0) {
					depth--;
					if (depth == 0) {
						writer.write(buf.toString());
						writer.write("\n");
						buf.setLength(0);
					}
				}

				currentPath.setLength(positions.pop());
				currentPathStr = currentPath.toString();
			} else if (event.isCharacters()) {
				if (depth > 0) {
					buf.append(event.asCharacters().getData());
				}
			}
		}

	}

}
