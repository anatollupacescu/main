package core.node.parent;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.namespace.QName;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQSequence;

import org.apache.commons.io.IOUtils;

import ch.ethz.mxquery.xqj.MXQueryXQDataSource;

public class Node {

	private static final String prefix = "declare variable $document external;\r\n";
	
	protected static final String error="error";
	protected static final String success="success";
	
	private final XQConnection xqconnection;
	
	protected final String query;
	
	public Node(String ... args) {
		XQConnection conn = null;
		String content = null;
		
		if(args != null && args.length > 0) {
			InputStream in = null;
			try {
				in = new FileInputStream(args[0]);
				content = prefix + IOUtils.toString(in);
				conn = new MXQueryXQDataSource().getConnection();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
		
		xqconnection = conn;
		query = content;
	}

	protected String executeQuery(String text) throws XQException, IOException {

		XQPreparedExpression exp = xqconnection.prepareExpression(query);
		exp.bindDocument(new QName("document"), text ,null, null);
		
		XQSequence xqsequence = exp.executeQuery();

		OutputStream result = new ByteArrayOutputStream();
		xqsequence.writeSequence(result, null);

		return result.toString().trim();
	}
}
