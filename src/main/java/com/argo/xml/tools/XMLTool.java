package com.argo.xml.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface XMLTool {
	void configure(String[] args);

	/**
	 * XML Input을 받아서 결과를 output으로 출력한다
	 * 
	 * @param input
	 * @param output
	 * @throws IOException
	 */
	void run(InputStream input, OutputStream output) throws IOException;
}
