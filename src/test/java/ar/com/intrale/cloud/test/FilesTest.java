package ar.com.intrale.cloud.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import ar.com.intrale.cloud.IntraleFactory;
import ar.com.intrale.cloud.UploadFunction;
import ar.com.intrale.cloud.UploadRequest;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.annotation.MicronautTest;

@MicronautTest(rebuildContext = true)
@Property(name = IntraleFactory.FACTORY, value = "true")
@Property(name = IntraleFactory.PROVIDER, value = "true")
public class FilesTest extends ar.com.intrale.cloud.Test {
	
	@Override
	public void beforeEach() {
	}

	@Override
	public void afterEach() {
	}

	@Test
	public void test() throws Exception {
		
		File file = new File("src/test/resources/multipart.txt");
		FileInputStream fileInputStream = new FileInputStream(file);
		
		UploadRequest uploadRequest = new UploadRequest();
		//uploadRequest.setContent(IOUtils.toString(fileInputStream));
		
		APIGatewayProxyRequestEvent event = makeRequestEvent(uploadRequest, UploadFunction.FUNCTION_NAME);
		event.setBody(IOUtils.toString(fileInputStream));
		event.getHeaders().put("content-type", "multipart/form-data; boundary=--427794312173857210843872");
		event.getHeaders().put("Content-Disposition", "form-data; name=\"prueba\"");
		
		APIGatewayProxyResponseEvent responseEvent = (APIGatewayProxyResponseEvent) lambda.execute(event);
		
		
	}

}
