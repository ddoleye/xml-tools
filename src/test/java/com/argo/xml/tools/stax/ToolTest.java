package com.argo.xml.tools.stax;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import com.argo.xml.tools.XMLTool;

public class ToolTest {

	private void exec(File target, XMLTool tool, String... args)
			throws XMLStreamException, IOException {
		InputStream input = new FileInputStream(target);
		OutputStream output = System.out;
		tool.configure(args);
		tool.run(input, output);
	}

	@Test
	public void test() throws XMLStreamException, IOException {
		File f = new File("lineitem.xml");

		exec(f, new NodeTreeTool());
		exec(f, new HeadTool(), "/table/T", "-10");
		exec(f, new SimpleExtractTool(), "/table/T");
	}
}
