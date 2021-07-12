package ar.com.intrale.cloud;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestStreamHandler;

@Introspected
public class LambdaStream extends MicronautRequestStreamHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseFunction.class);
	
	@Override
	protected String resolveFunctionName(Environment env) {
		return "files";
	}



	
/*	@Override
	public void execute(InputStream inputStream, OutputStream outputStream) throws IOException {
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
	}*/


}
