package ar.com.intrale.cloud;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;

import io.micronaut.function.aws.MicronautRequestStreamHandler;

public class LambdaStream extends MicronautRequestStreamHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseFunction.class);
	
	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		LOGGER.info("LambdaStream: START");        
		Charset encoding = Charset.forName("UTF-8");
        String input = IOUtils.toString(inputStream, encoding);
        LOGGER.info("LambdaStream: " + input);
        String output = "My first request handler: " + input;
        IOUtils.write(output, outputStream, encoding);
        LOGGER.info("LambdaStream: FINISH");
	}

}
