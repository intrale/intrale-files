package ar.com.intrale;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import ar.com.intrale.messages.RequestRoot;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.annotation.MicronautTest;
import io.netty.handler.codec.http.HttpResponseStatus;

@MicronautTest(rebuildContext = true)
@Property(name = IntraleFactory.FACTORY, value = "true")
@Property(name = IntraleFactory.PROVIDER, value = "true")
public class FilesTest extends ar.com.intrale.Test {
	
	@Override
	public void beforeEach() {
	}

	@Override
	public void afterEach() {
	}

	//TODO: NO DEJAR ESTO ASI, NO VALIDA LA FUNCIONALIDAD
	//@Test
	public void test() throws Exception {
		
		File file = new File("src/test/resources/multipart.txt");
		FileInputStream fileInputStream = new FileInputStream(file);
		
		APIGatewayProxyRequestEvent event = makeRequestEvent(new UploadRequest(), UploadFunction.FUNCTION_NAME);
		event.setBody(IOUtils.toString(fileInputStream));
		event.getHeaders().put(FunctionBuilder.HEADER_CONTENT_TYPE, "multipart/form-data; boundary=--427794312173857210843872");
		event.getHeaders().put(UploadFunction.FILENAME, "prueba.jpg");
		
		APIGatewayProxyResponseEvent responseEvent = (APIGatewayProxyResponseEvent) lambda.execute(event);
		
		assertEquals(HttpResponseStatus.OK.code(), responseEvent.getStatusCode());
		
		event = makeRequestEvent(new RequestRoot(), FunctionConst.READ);
		//event.setBody(IOUtils.toString(fileInputStream));
		Map<String, String> pathParameters = new HashMap<String, String>();
		pathParameters.put(FunctionBuilder.HEADER_BUSINESS_NAME, "INTRALE");
		pathParameters.put(UploadFunction.FILENAME, "prueba.jpg");
		event.setPathParameters(pathParameters);
		
		responseEvent = (APIGatewayProxyResponseEvent) lambda.execute(event);
		
		assertEquals(HttpResponseStatus.OK.code(), responseEvent.getStatusCode());
		
	}

}
