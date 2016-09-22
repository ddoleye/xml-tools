package com.argo.xml.tools.stax;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Stack;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.argo.xml.tools.XMLTool;

/**
 *
 */
public class NodeTreeTool extends StAXBase implements XMLTool {

	private StringBuilder buf = new StringBuilder();
	private boolean showValue = true;
	private boolean showCount = true;

	public NodeTreeTool() {
	}

	@Override
	public void configure(String[] args) {
		for (String arg : args) {
			if ("-x".equals(arg)) {
				showCount = false;
			} else if ("-v".equals(arg)) {
				showValue = false;
			}
		}
	}

	@Override
	public void run(InputStream input, OutputStream output) throws IOException {
		try {
			Object[] stats = new Object[1];
			stats[0] = Integer.valueOf(0);
			NodeCount root = collect(input, stats);

			if (root == null) {
				// error?
			} else {
				print(root, output, (Integer) stats[0]);
			}
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

	private NodeCount collect(InputStream input, Object[] stats)
			throws XMLStreamException {
		NodeCount root = null;
		NodeCount current = null;

		XMLEventReader reader = createReader(input);
		try {
			Stack<NodeCount> stack = new Stack<NodeCount>();
			while (reader.hasNext()) {
				XMLEvent event = reader.nextEvent();
				if (event.isStartDocument()) {
					// log.debug("StartDocument:{}", event);
				} else if (event.isStartElement()) {
					StartElement element = event.asStartElement();
					String name = element.getName().getLocalPart();

					if (stack.isEmpty()) {
						// 최상위 노드 생성
						root = new NodeCount(name);
						stack.push(root);
					} else {
						// 현재 노드에 자식 추가
						NodeCount child = stack.peek().addChild(name);
						// 자식을 스택에도 추가
						stack.push(current = child);
					}

					if (stack.size() > (Integer) stats[0])
						stats[0] = Integer.valueOf(stack.size());

				} else if (event.isEndElement()) {
					// 종료태그일 경우는 스택에서 노드 제거
					if (buf.length() > 0) {
						current.setValue(buf.toString().trim());
						buf.setLength(0);
					}
					current = stack.pop();
				} else if (event.isCharacters()) {
					if (current != null) {
						buf.append(event.asCharacters().getData());
					}
				}
			}
		} finally {
			closeQuietly(reader);
		}
		return root;
	}

	private void print(NodeCount root, OutputStream output, int maxdepth)
			throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(output);
		try {
			print(root, 0, writer, maxdepth);
		} finally {
			writer.flush();
		}
	}

	private void tab(int depth, Writer out) throws IOException {
		for (int ii = 0; ii < depth; ii++) {
			out.write("\t");
		}
	}

	private void print(NodeCount node, int depth, Writer out, int maxdepth)
			throws IOException {
		tab(depth, out);
		out.write(node.getName());

		if (showCount || showValue) {
			if (depth < maxdepth) {
				tab(maxdepth - depth, out);
			}
		}
		if (showCount) {
			out.write("\t");
			out.write(String.valueOf(node.getCount()));
		}
		if (showValue) {
			String value = node.getValue();
			if (value != null) {
				out.write("\t");
				if (!value.startsWith("http://")
						&& !value.startsWith("https://") && value.length() > 40)
					out.write(value.substring(0, 40));
				else
					out.write(value);
			}
		}
		out.write("\n");
		for (NodeCount child : node.getChildren()) {
			print(child, depth + 1, out, maxdepth);
		}
	}
}
