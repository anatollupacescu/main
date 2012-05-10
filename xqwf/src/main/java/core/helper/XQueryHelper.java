package core.helper;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.xml.namespace.QName;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQSequence;

import ch.ethz.mxquery.xqj.MXQueryXQDataSource;

import nu.xom.Document;
import core.Const;

public class XQueryHelper {

	private static final MXQueryXQDataSource mxqueryDataSource = new MXQueryXQDataSource();
	
	public static Document processQuery(String query, Document document) throws XQException {
		XQConnection xqconnection = mxqueryDataSource.getConnection();
		XQPreparedExpression exp = xqconnection.prepareExpression(query);
		exp.bindDocument(new QName(Const.DOCUMENT), document.toXML(), null,null);
		XQSequence xqsequence = exp.executeQuery();
		OutputStream result = new ByteArrayOutputStream();
		xqsequence.writeSequence(result, null);
		return DocumentHelper.createDocumentFromString(result.toString());
	}
}
