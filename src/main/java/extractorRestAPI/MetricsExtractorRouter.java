package extractorRestAPI;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import bpmnMetadataExtractor.BpmnModelReader;

import org.json.JSONObject;


@Path("/api")
public class MetricsExtractorRouter {
	@Path("/fileUpload")
	@GET
	@Produces("application/json")
	public Response extractMetrics() { 
		JSONObject json = new JSONObject();
		json.put("Hello", "World!");
		String prova = "Ecco il json " + json;
		return Response.status(200).entity(prova).build();
	}
}