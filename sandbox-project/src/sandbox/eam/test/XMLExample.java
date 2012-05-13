package sandbox.eam.test;

import javax.xml.namespace.QName;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;
import javax.xml.xquery.XQSequence;

import ch.ethz.mxquery.xqj.MXQueryXQDataSource;

public class XMLExample {

	public static void main(String[] args) throws XQException {
//		String query = "declare variable $document external;\r\ncount($document/j)";
		String query = "declare variable $document external;\r\n<c>{count($document/request) > 0 } </c>\r\n";
		XQDataSource ds = new MXQueryXQDataSource();
		XQConnection conn = ds.getConnection();

		System.out.println("Running query: " + query);

		XQPreparedExpression exp = conn.prepareExpression(query);
		exp.bindDocument(new QName("document"), "<?xml version=\"1.0\"?><request />" ,null, null);
		XQResultSequence result = exp.executeQuery();
		XQSequence sequence = conn.createSequence(result);

		sequence.writeSequence(System.out, null);

		result.close();
		sequence.close();
	}

}