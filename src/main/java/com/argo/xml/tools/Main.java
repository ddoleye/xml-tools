package com.argo.xml.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import com.argo.xml.tools.stax.NodeTreeTool;
import com.argo.xml.tools.stax.SimpleExtractTool;
import com.argo.xml.tools.stax.HeadTool;

public class Main {
	public static void main(String[] args) {

		// 파라미터는 두개 이상
		if (args.length < 2) {
			System.out.println(" command [옵션] XML파일");
			System.exit(-1);
		}

		// 마지막 파라미터는 파일
		File file = new File(args[args.length - 1]);
		if (!file.exists() || !file.isFile() || !file.canRead()) {
			System.err.println(" 처리할 수 없는 파일 [" + file.getAbsolutePath() + "]");
			System.exit(-1);
		}

		XMLTool tool = null;

		if ("tree".equals(args[0])) {
			tool = new NodeTreeTool();
		} else if ("extract".equals(args[0])) {
			tool = new SimpleExtractTool();
		} else if ("head".equals(args[0])) {
			tool = new HeadTool();
		}

		// 처리할 수 없는 명령어
		if (tool == null) {
			System.err.println(" 알수 없는 command [" + args[0] + "]");
			System.exit(-1);
		}

		if (args.length > 2) {
			String[] options = Arrays.copyOfRange(args, 1, args.length - 1);
			try {
				tool.configure(options);
			} catch (IllegalArgumentException e) {
				System.err.println(" 처리할 수 없는 옵션 [" + Arrays.toString(options)
						+ "]");
				e.printStackTrace(System.err);
				System.exit(-1);
			}
		}

		// run
		FileInputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(file);
			output = System.out;

			tool.run(input, output);

		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}
}
