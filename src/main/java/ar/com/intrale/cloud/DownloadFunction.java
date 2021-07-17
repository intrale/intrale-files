package ar.com.intrale.cloud;

import java.util.Base64;

import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;

import ar.com.intrale.cloud.exceptions.FunctionException;

@Singleton
@Named(FunctionConst.READ)
public class DownloadFunction extends BaseFunction<Request, DownloadResponse, AmazonS3, DownloadStringToRequestBuilder, DownloadFunctionResponseToHttpResponseBuilder> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadFunction.class);

	@Override
	public DownloadResponse execute(Request request) throws FunctionException {
		LOGGER.info("DownloadFunction execute");
		DownloadResponse response = new DownloadResponse();
		try {
			String filename = request.getPathParameters().get("filename");
			S3Object file = provider.getObject(config.getS3().getBucketName(), filename);
			
			byte[] fileBytes = file.getObjectContent().readAllBytes();
			
			response.setContent(Base64.getEncoder().encodeToString(fileBytes));
			
		} catch (Exception e) {
			LOGGER.error(FunctionException.toString(e));
		}
		return response;		
	}


}
