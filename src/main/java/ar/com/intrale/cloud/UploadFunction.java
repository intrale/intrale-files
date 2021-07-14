package ar.com.intrale.cloud;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;

import ar.com.intrale.cloud.exceptions.FunctionException;
import delight.fileupload.FileUpload;

@Singleton
@Named(UploadFunction.FUNCTION_NAME)
public class UploadFunction extends BaseFunction<UploadRequest, Response, AmazonS3, StringToUploadRequestBuilder, FunctionResponseToHttpResponseBuilder> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadFunction.class);
	
	public static final String FUNCTION_NAME = "upload";
	
	@Override
	public Response execute(UploadRequest request) throws FunctionException {
	     //Create the logger
        LOGGER.info("Ejecutando UploadFunction");
        
        //Log the length of the incoming body
        LOGGER.info("the length of the incoming body:" + String.valueOf(request.getContent().getBytes().length));

        //Create the APIGatewayProxyResponseEvent response
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        //Set up contentType String
        String contentType = "";
        
        try {
        	
            contentType = request.getHeaders().get(FunctionBuilder.HEADER_CONTENT_TYPE);
            LOGGER.info("contentType:" + contentType);
            
            byte[] base64 =  Base64.getDecoder().decode(request.getContent().getBytes(StandardCharsets.UTF_8));
            
            List<FileItem> files = FileUpload.parse(base64, "multipart/form-data");
            files.forEach(new Consumer<FileItem>() {

				@Override
				public void accept(FileItem file) {
					LOGGER.info("file.getName():" + file.getName());
				}
			});
            
            
            LOGGER.info("Finalizando");
            
        } 
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't  
            // process it, so it returned an error response.
            LOGGER.error(e.getMessage());
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            LOGGER.error(e.getMessage());
        } 
        /*catch (IOException e) {
            // Handle MultipartStream class IOException
            LOGGER.error(e.getMessage());
        }*/

        LOGGER.info(response.toString());
        return new Response();
    }

	

}
