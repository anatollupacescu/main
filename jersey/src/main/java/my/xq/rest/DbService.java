package my.xq.rest;

import java.io.StringWriter;
import java.io.Writer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
@Path("/db")
public class DbService {

	@Autowired
	private JdbcOperations jdbc;
	
	@GET
	@Path("/query/{string}")
	@Produces(MediaType.APPLICATION_XML_VALUE)
	public synchronized Response query(@PathParam("string") String select) {
		try {
			SqlRowSet srs = jdbc.queryForRowSet(select);
			
			XMLOutputFactory xof =  XMLOutputFactory.newInstance();
			Writer stream = new StringWriter();
			XMLStreamWriter xtw = xof.createXMLStreamWriter(stream);

			xtw.writeStartDocument();
			xtw.writeStartElement(srs.getMetaData().getTableName(1));
			
		    while (srs.next()) {
				for (String column : srs.getMetaData().getColumnNames()) {
					xtw.writeStartElement(column);
					xtw.writeCharacters(srs.getString(column));
					xtw.writeEndElement();
				}
		    }
		    
		    xtw.writeEndElement();
			xtw.writeEndDocument();
			xtw.flush();
			xtw.close();
			
			return Response.status(200).entity(stream.toString()).build();
		} catch (Exception e) {
			return Response.status(201).entity("<root><code>" + e.getMessage() + "</code></root>").build();
		}
	}
	
	@GET
	@Path("/update/{statement}")
	public Response update(@PathParam("statement") String statement) {
		try {
			jdbc.update(statement);
			return Response.status(200).entity("ok").build();
		} catch (Exception e) {
			return Response.status(201).entity(e.getMessage()).build();
		}
	}
}