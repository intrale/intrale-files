package ar.com.intrale.cloud;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileUploadIOException;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.fileupload.MultipartStream.MalformedStreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import ar.com.intrale.cloud.exceptions.FunctionException;
import delight.fileupload.FileUpload;
import net.minidev.json.JSONObject;

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
        
        //Every file will be named image.jpg in this example. 
        //You will want to do something different here in production
        String fileObjKeyName = String.valueOf(System.currentTimeMillis()) + ".jpg";   

        try {
        	
            //Get the uploaded file and decode from base64
            byte[] base64Content = Base64.decodeBase64(request.getContent().getBytes());
            LOGGER.info("Get the uploaded file and decode from base64 length:" + String.valueOf(base64Content.length));
            
            contentType = request.getHeaders().get(FunctionBuilder.HEADER_CONTENT_TYPE);
            
            List<FileItem> files = FileUpload.parse(base64Content, contentType);
            files.forEach(new Consumer<FileItem>() {

				@Override
				public void accept(FileItem file) {
					LOGGER.info("file.getName():" + file.getName());
				}
			});
            
            
            LOGGER.info("Finalizando");
            
            //oldVersion(request, response, contentType, fileObjKeyName, base64Content);

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

	protected void oldVersion(UploadRequest request, APIGatewayProxyResponseEvent response, String contentType,
			String fileObjKeyName, byte[] base64Content)
			throws UnsupportedEncodingException, IOException, FileUploadIOException, MalformedStreamException {
		byte[] boundary = getBoundary(request, contentType, base64Content); 
		
		//Create a ByteArrayInputStream
		ByteArrayInputStream content = new ByteArrayInputStream(base64Content);
		
		LOGGER.info("Create a ByteArrayInputStream:" + content.readAllBytes().length);
		
		//Create a MultipartStream to process the form-data
		/*MultipartStream multipartStream =
		  new MultipartStream(content, boundary, boundary.length, null);*/
		int bufSize = base64Content.length;
		LOGGER.info("bufSize:" + bufSize);
		MultipartStream multipartStream =
		        new MultipartStream(content, boundary, bufSize, null);
		

		//Create a ByteArrayOutputStream
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		//Find first boundary in the MultipartStream
		boolean nextPart = multipartStream.skipPreamble();
		LOGGER.info("multipartStream.skipPreamble():" + nextPart);
		
		//Loop through each segment
		while (nextPart) {
		    String header = multipartStream.readHeaders();
		    
		    //Log header for debugging
		    LOGGER.info("Headers:");
		    LOGGER.info(header);
		    
		    //Write out the file to our ByteArrayOutputStream
		    multipartStream.readBodyData(out);
		    //Get the next part, if any
		    nextPart = multipartStream.readBoundary();
		}
		
		//Log completion of MultipartStream processing
		LOGGER.info("Data written to ByteStream");
		
		//Prepare an InputStream from the ByteArrayOutputStream
		InputStream fis = new ByteArrayInputStream(out.toByteArray());
		LOGGER.info("InputStream length:" + fis.readAllBytes().length);
		
		//Create our S3Client Object
		/*AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
		        .withRegion(config.getAws().getRegion())
		        .build();*/
   
		//Configure the file metadata
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(out.toByteArray().length);
		metadata.setContentType("image/jpeg");
		metadata.setCacheControl("public, max-age=31536000");
		
		//Put file into S3
		provider.putObject(config.getS3().getBucketName(), fileObjKeyName, fis, metadata);
         
		//Log status
		LOGGER.info("Put object in S3");

		//Provide a response
		response.setStatusCode(200);
		Map<String, String> responseBody = new HashMap<String, String>();
		responseBody.put("Status", "File stored in S3");
		String responseBodyString = new JSONObject(responseBody).toJSONString();
		response.setBody(responseBodyString);
	}

	protected byte[] getBoundary(UploadRequest request, String contentType, byte[] base64Content)
			throws UnsupportedEncodingException {
		//Get the content-type header and extract the boundary
		LOGGER.info("HEADERS:" + request.getHeaders());
		
		Map<String, String> hps = request.getHeaders();
		if (hps != null) {
		    contentType = hps.get(FunctionBuilder.HEADER_CONTENT_TYPE);
		    LOGGER.info(FunctionBuilder.HEADER_CONTENT_TYPE + ":" + contentType);
		}
		String[] boundaryArray = contentType.split("=");
		
		//Transform the boundary to a byte array
		byte[] boundary = boundaryArray[1].getBytes();
		
		//byte[] boundary = request.getHeaders().get(FunctionBuilder.HEADER_CONTENT_TYPE).getBytes();
		LOGGER.info("boundary:" + new String(boundary));
		
		return boundary;
	}
	

}
