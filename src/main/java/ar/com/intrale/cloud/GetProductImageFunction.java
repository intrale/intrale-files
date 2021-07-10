package ar.com.intrale.cloud;

import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import ar.com.intrale.cloud.exceptions.FunctionException;

@Singleton
@Named(GetProductImageFunction.FUNCTION_NAME)
public class GetProductImageFunction extends BaseFunction<GetProductImageRequest, S3ObjectInputStream, AmazonS3, StringToGetProductImageRequestBuilder, ObjectToSameBuilder> {

	private static final Logger LOGGER = LoggerFactory.getLogger(GetProductImageFunction.class);
	
	public static final String FUNCTION_NAME = "getImage";

	@Override
	public S3ObjectInputStream execute(GetProductImageRequest request) throws FunctionException {
		LOGGER.info("GetProductImageFunction execute");
		return provider.getObject(config.getS3().getBucketName(), request.getId()).getObjectContent();
	}


}
