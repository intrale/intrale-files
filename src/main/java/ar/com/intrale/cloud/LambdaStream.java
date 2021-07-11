package ar.com.intrale.cloud;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;

import ar.com.intrale.cloud.config.ApplicationConfig;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestStreamHandler;

@Introspected
public class LambdaStream extends MicronautRequestStreamHandler {


	private static final Logger LOGGER = LoggerFactory.getLogger(BaseFunction.class);
	
	@Inject
	protected ApplicationConfig config;
	
	@Inject
	private AmazonS3 provider;


	public LambdaStream() {
		super();
		LOGGER.info("LambdaStream: constructor");   
		applicationContext.inject(this);
	}
	
	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		LOGGER.info("LambdaStream: START");        
		Charset encoding = Charset.forName("UTF-8");
        String input = IOUtils.toString(inputStream, encoding);
        LOGGER.info("LambdaStream: " + input);
        
        LOGGER.info("config: " + config);
        LOGGER.info("provider: " + provider);
        String bucketName = config.getS3().getBucketName();
        S3Object s3Object = provider.getObject(bucketName, "12afd795-09e1-47f8-8ed1-1ae1b0216d83");
        
        
        IOUtils.copy(s3Object.getObjectContent(), outputStream);
        
        //String output = "My first request handler: " + input;
        //IOUtils.write(output, outputStream, encoding);
        LOGGER.info("LambdaStream: END");
	}


}
