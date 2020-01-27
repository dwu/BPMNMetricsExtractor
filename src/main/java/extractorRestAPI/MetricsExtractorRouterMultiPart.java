package extractorRestAPI;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import bpmnMetadataExtractor.BpmnModelReader;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/api")
public class MetricsExtractorRouterMultiPart {
	@Path("/fileUploadMultiPart")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response extractMetrics(
			@FormDataParam("model") InputStream uploadedInputStream,
			@FormDataParam("model") FormDataContentDisposition fileDetail) throws IOException {
		BpmnModelReader metricsExtractor = new BpmnModelReader();
//		String fileName = fileDetail.getFileName().substring(0, fileDetail.getFileName().lastIndexOf('.'));
		
		String text = IOUtils.toString(uploadedInputStream, StandardCharsets.UTF_8.name());
		
//		System.out.println("TO UTF-8: "+text);
		
		InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
		
		String fileName = "ExtractedMetadata";
		String json = metricsExtractor.getJsonMetrics(stream, fileName);
		return  Response.ok((Object)json).
				header("Content-Disposition","attachment; filename = " + fileName + ".json").
				build();
	}
}

