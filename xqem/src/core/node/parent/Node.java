package core.node.parent;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQSequence;

import org.apache.commons.io.IOUtils;

import ch.ethz.mxquery.xqj.MXQueryXQDataSource;

public class Node {

	protected static final String error="error";
	protected static final String success="success";
	
	protected final String query;
	
	public Node(String ... args) {
		
		String content = null;
		
		if(args != null && args.length > 0) {
			InputStream in = null;
			try {
				in = new FileInputStream(args[0]);
				content = IOUtils.toString(in);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
		
		query = content;
	}

	protected String executeQuery(String text) throws XQException, IOException {

		XQDataSource xqdatasource = new MXQueryXQDataSource();
		XQConnection xqconnection = xqdatasource.getConnection();

		StringBuilder q = new StringBuilder();
		q.append("let $document := \n");
		q.append(text);
		q.append("\n");
		q.append(query);

		XQExpression xqexpression = xqconnection.createExpression();
		XQSequence xqsequence = xqexpression.executeQuery(q.toString());

		OutputStream result = new ByteArrayOutputStream();
		xqsequence.writeSequence(result, null);

		return result.toString().trim();
	}
}
