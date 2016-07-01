package com.argo.xml.tools.stax;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

public abstract class StAXBase {
	private XMLInputFactory inputFactory = null;
	private XMLOutputFactory outputFactory = null;

	public XMLEventReader createReader(InputStream input, String encoding)
			throws XMLStreamException {
		if (inputFactory == null) {
			inputFactory = XMLInputFactory.newInstance();
			// configurable?
			inputFactory
					.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
		}

		return inputFactory.createXMLEventReader(input, encoding);
	}

	public XMLEventReader createReader(InputStream input)
			throws XMLStreamException {
		if (inputFactory == null) {
			inputFactory = XMLInputFactory.newInstance();
			// configurable?
			inputFactory
					.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
		}

		return inputFactory.createXMLEventReader(input);
	}

	public XMLEventWriter createWriter(OutputStream output, String encoding)
			throws XMLStreamException {
		if (outputFactory == null) {
			outputFactory = XMLOutputFactory.newInstance();
		}

		return outputFactory.createXMLEventWriter(output, encoding);
	}

	public void closeQuietly(XMLEventReader reader) {
		if (reader == null)
			return;

		try {
			reader.close();
		} catch (XMLStreamException e) {
			e.printStackTrace(System.err);
		}
	}

	public void closeQuietly(Closeable closeable) {
		if (closeable == null)
			return;

		try {
			closeable.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	public void closeQuietly(XMLEventWriter writer) {
		if (writer == null)
			return;

		try {
			writer.close();
		} catch (XMLStreamException e) {
			e.printStackTrace(System.err);
		}
	}
}
