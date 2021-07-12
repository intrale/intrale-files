package ar.com.intrale.cloud;

import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import ar.com.intrale.cloud.exceptions.FunctionException;

@Singleton
@Named(DownloadFunction.FUNCTION_NAME)
public class DownloadFunction extends BaseFunction<DownloadRequest, S3ObjectInputStream, AmazonS3, StringToGetProductImageRequestBuilder, FunctionResponseToHttpResponseBuilder> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadFunction.class);
	
	public static final String FUNCTION_NAME = "download";

	@Override
	public S3ObjectInputStream execute(DownloadRequest request) throws FunctionException {
		LOGGER.info("GetProductImageFunction execute");
		return provider.getObject(config.getS3().getBucketName(), request.getId()).getObjectContent();
	}


}
