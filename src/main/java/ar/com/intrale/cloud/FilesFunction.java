package ar.com.intrale.cloud;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.function.Function;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;

import ar.com.intrale.cloud.config.ApplicationConfig;
import ar.com.intrale.cloud.exceptions.FunctionException;
import io.micronaut.function.FunctionBean;

@FunctionBean("files")
public class FilesFunction implements Function<InputStream, OutputStream> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseFunction.class);
	
	@Inject
	protected ApplicationConfig config;
	
	@Inject
	private AmazonS3 provider;
	
	@Override
	public OutputStream apply(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
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
	        LOGGER.info("LambdaStream: FINISH");
		} catch (Exception e) {
			LOGGER.error(FunctionException.toString(e));
		}
		return outputStream;
	}

}
