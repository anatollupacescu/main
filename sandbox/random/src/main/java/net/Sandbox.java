package net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;

public class Sandbox {

	public static void main(String[] args) throws IOException, InterruptedException {
		Process process = Runtime.getRuntime().exec("term");
		Thread.sleep(5000);
		InputStream stream = process.getInputStream();
		String content = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
		Closeables.close(stream, true);
		System.out.println(content);
	}
}
