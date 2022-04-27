package ar.com.intrale;

import java.io.File;
import java.util.Base64;

import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;

import ar.com.intrale.exceptions.FunctionException;
import ar.com.intrale.messages.RequestRoot;

@Singleton
@Named(FunctionConst.READ)
public class DownloadFunction extends BaseFunction<RequestRoot, DownloadResponse, AmazonS3, DownloadStringToRequestBuilder, DownloadFunctionResponseToHttpResponseBuilder> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadFunction.class);

	@Override
	public DownloadResponse execute(RequestRoot request) throws FunctionException {
		LOGGER.info("DownloadFunction execute");
		DownloadResponse response = new DownloadResponse();
		try {
			String filename =  request.getPathParameters().get(FunctionConst.BUSINESS_NAME.toLowerCase()) + File.separator + request.getPathParameters().get(UploadFunction.FILENAME);
			S3Object file = provider.getObject(config.getS3().getBucketName(), filename);
			
			byte[] fileBytes = file.getObjectContent().readAllBytes();
			String encodeFile = Base64.getEncoder().encodeToString(fileBytes);
			//String decodedFile = new String (Base64.getDecoder().decode(encodeFile));
			response.setContent(encodeFile);
			
		} catch (Exception e) {
			LOGGER.error(FunctionException.toString(e));
		}
		return response;		
	}


}
