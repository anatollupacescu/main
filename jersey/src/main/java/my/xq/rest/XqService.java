package my.xq.rest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;
import javax.xml.xquery.XQSequence;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import ch.ethz.mxquery.xqj.MXQueryXQDataSource;

@Component
@Path("/xq")
public class XqService {

	private final static String path = "src/main/resources";
	private final static XQDataSource ds = new MXQueryXQDataSource();
	
	@GET
	@Path("/{flow}/{file}")
	public Response go(@Context UriInfo info, @PathParam("flow") String flow, @PathParam("file") String file) throws XQException, FileNotFoundException, IOException, XMLStreamException {
		
		MultivaluedMap<String, String> map = info.getQueryParameters();
		Set<String> keys = map.keySet();
		
		XMLOutputFactory xof =  XMLOutputFactory.newInstance();
		Writer stream = new StringWriter();
		XMLStreamWriter xtw = xof.createXMLStreamWriter(stream);
		
		xtw.writeStartElement("root");
		
		for(String key : keys) {
			xtw.writeStartElement(key);
			xtw.writeCharacters(map.get(key).get(0));
			xtw.writeEndElement();
		}
		
		xtw.writeEndElement();
		xtw.flush();
		xtw.close();
		
		String previous = stream.toString();
		
		while (file != null) {
			String fullPath = path + "/" + flow + "/" + file + ".xql";
			String query = IOUtils.toString(new FileInputStream(fullPath));
			
			StringBuilder fullQuery =  new StringBuilder();
			fullQuery.append("declare variable $prev :=");
			fullQuery.append(previous);
			fullQuery.append(";\n\n");
			fullQuery.append(query);
			
			System.out.println(fullQuery.toString());
			
			XQConnection conn = ds.getConnection();
			XQPreparedExpression exp = conn.prepareExpression(fullQuery.toString());
			XQResultSequence result = exp.executeQuery();
			XQSequence sequence = conn.createSequence(result);
			sequence.next();
			XMLStreamReader reader = sequence.getItemAsStream();
			file = getCode(reader);
			sequence.first();
			if(file == null) {
				return Response.status(200).entity(sequence.getItemAsString(null)).build();
			}
			previous = sequence.getItemAsString(null);
			result.close();
			sequence.close();
		}

		return Response.status(200).entity("done").build();
	}
	
	private String getCode(XMLStreamReader reader) throws XMLStreamException {
		while (reader.hasNext()) {
			int type = reader.getEventType(); 
			if (type == XMLStreamConstants.START_ELEMENT) {
				if("_code".equals(reader.getLocalName())) {
					reader.next();
					return reader.getText();
				}
			}
			reader.next();
		}
		return null;
	}
}