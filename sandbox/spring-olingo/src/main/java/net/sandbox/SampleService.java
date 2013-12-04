package net.sandbox;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

@Path("/cima-sig/{resourcePath : (\\w.*)}")
public class SampleService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response savePayment(@PathParam("resourcePath") String resourcePath) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("name", "jora");
			return Response.status(200).entity(SResponses.entities(ImmutableSet.of(map))).build();
	}
}
