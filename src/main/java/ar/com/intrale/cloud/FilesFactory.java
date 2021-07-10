package ar.com.intrale.cloud;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Requires;

public class FilesFactory extends IntraleFactory<AmazonS3> {

	@Bean @Requires(property = "app.instantiate.s3provider", value = IntraleFactory.TRUE, defaultValue = IntraleFactory.TRUE)
	@Override
	public AmazonS3 provider() {
		BasicAWSCredentials credentials = new BasicAWSCredentials(config.getS3().getAccess(), config.getS3().getSecret());
    	
		AmazonS3 amazonDynamoDB = AmazonS3ClientBuilder.standard()
          .withCredentials(new AWSStaticCredentialsProvider(credentials))
          .withRegion(config.getAws().getRegion())
          .build();
         
        return amazonDynamoDB;
	}

}
