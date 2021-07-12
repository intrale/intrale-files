package ar.com.intrale.cloud;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.MultipartStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import ar.com.intrale.cloud.exceptions.FunctionException;
import net.minidev.json.JSONObject;

public class UploadFunction extends BaseFunction<UploadRequest, String, AmazonS3, StringToUploadRequestBuilder, FunctionResponseToHttpResponseBuilder> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadFunction.class);
	
	@Override
	public String execute(UploadRequest request) throws FunctionException {
		LOGGER.info("Loading Java Lambda handler of Proxy");
        
        //Log the length of the incoming body
		LOGGER.info(String.valueOf(request.getContent().getBytes().length));

        //Create the APIGatewayProxyResponseEvent response
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        //Set up contentType String
        String contentType = "";
        
        //Change these values to fit your region and bucket name
        //String clientRegion = "YOUR-REGION";
        //String bucketName = "YOUR-BUCKET-NAME";
        
        //Every file will be named image.jpg in this example. 
        //You will want to do something different here in production
        String fileObjKeyName = "image.jpg";   

        try {
        	
            //Get the uploaded file and decode from base64
            byte[] bI = Base64.decodeBase64(request.getContent().getBytes());
            
            //Get the content-type header and extract the boundary
            Map<String, String> hps = null ;//= headers;
            if (hps != null) {
                contentType = hps.get("content-type");
            }
            String[] boundaryArray = contentType.split("=");
            
            //Transform the boundary to a byte array
            byte[] boundary = boundaryArray[1].getBytes();
        	
            //Log the extraction for verification purposes
            LOGGER.info(new String(bI, "UTF-8") + "\n"); 
            
            //Create a ByteArrayInputStream
            ByteArrayInputStream content = new ByteArrayInputStream(bI);
            
            //Create a MultipartStream to process the form-data
            MultipartStream multipartStream =
              new MultipartStream(content, boundary, bI.length, null);
        	
            //Create a ByteArrayOutputStream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
        	
            //Find first boundary in the MultipartStream
            boolean nextPart = multipartStream.skipPreamble();
            
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
        	
            //Create our S3Client Object
            /*AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
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
            
            return responseBodyString;

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
        catch (IOException e) {
            // Handle MultipartStream class IOException
        	LOGGER.error(e.getMessage());
        }

        LOGGER.info(response.toString());
        return null;
    }
	

}
