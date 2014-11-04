package mr.serioja;

import org.apache.camel.builder.RouteBuilder;

public class FtpUploadRoute extends RouteBuilder {

	private final static String ftp_client = "ftp://admin:admin@localhost:21/out/?fileName=${file:name}&tempFileName=${file:onlyname}.part&stepwise=false";
	private final static String ftp_server = ftp_client + "&delay=5s&move=done";

	@Override
	public void configure() throws Exception {
		from(ftp_server).to("file:inputdir/download").log("Downloaded file ${file:name} complete.");

		from("file:inputdir/upload?moveFailed=../error")
			.log("Uploading file ${file:name}")
			.to(ftp_client)
			.log("Uploaded file ${file:name} complete.")
		.end();
	}
}
