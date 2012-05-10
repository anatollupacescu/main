package net.xqwf.helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.xqwf.Const;

import org.apache.commons.io.IOUtils;


public class MiscHelper {

	public static String readFile(String fileName) {
		InputStream in = null;
		String fileContents = null;
		try {
			in = new FileInputStream(fileName);
			fileContents = Const.XQUERY_PREFIX + IOUtils.toString(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}
		return fileContents;
	}
}
