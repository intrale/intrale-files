package ar.com.intrale.cloud;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import ar.com.intrale.cloud.exceptions.FunctionException;

@Singleton
@Named(UploadFunction.FUNCTION_NAME)
public class UploadFunction extends BaseFunction<UploadRequest, Response, AmazonS3, StringToUploadRequestBuilder, FunctionResponseToHttpResponseBuilder> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadFunction.class);
	
	public static final String FUNCTION_NAME = "upload";
	
	@Override
	public Response execute(UploadRequest request) throws FunctionException {
		LOGGER.info("Ejecutando funcion para el upload de archivos");
        
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
        	
            //Obtenga el archivo cargado y decodifique desde base64
            byte[] decodeContent = Base64.decodeBase64(request.getContent().getBytes());
            LOGGER.info("decodeContent.length:" + decodeContent.length);
            
            //Obtenga el encabezado de tipo de contenido y extraiga el límite
            contentType = request.getHeaders().get(FunctionBuilder.HEADER_CONTENT_TYPE);
            LOGGER.info("contentType:" + contentType);
            
            //String[] boundaryArray = contentType.split("=");
            
            //Transforma el límite en una matriz de bytes
            //byte[] boundary = boundaryArray[0].getBytes();
        	
            //Registre la extracción con fines de verificación
            //LOGGER.info("decodeContent:" + new String(decodeContent, "UTF-8") + "\n"); 
            
            //Crear un ByteArrayInputStream
            ByteArrayInputStream content = new ByteArrayInputStream(decodeContent);
        	
            //Crear un ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            //Cree un MultipartStream para procesar los datos del formulario
            /*MultipartStream multipartStream =
              new MultipartStream(content, contentType.getBytes(), decodeContent.length, null);
            
            //Encuentra el primer límite en MultipartStream
            boolean nextPart = multipartStream.skipPreamble();
            
            //Recorre cada segmento
            while (nextPart) {
                String header = multipartStream.readHeaders();
                
                //Encabezado de registro para depuración
                LOGGER.info("Headers:");
                LOGGER.info(header);
                
                //Escriba el archivo en nuestra ByteArrayOutputStream
                multipartStream.readBodyData(outputStream);
                //Obtenga la siguiente parte, si corresponde
                nextPart = multipartStream.readBoundary();
            }
            
            //Finalización del registro del procesamiento MultipartStream
            LOGGER.info("Data written to ByteStream");
            
            //Prepare un InputStream desde ByteArrayOutputStream
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
     
            //Configurar los metadatos del archivo
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(outputStream.toByteArray().length);
            metadata.setContentType("image/jpeg");
            metadata.setCacheControl("public, max-age=31536000");*/

            IOUtils.write(decodeContent, outputStream);
            LOGGER.info("ContentLength:" + outputStream.toByteArray().length);
            
            //Ponga el archivo en S3
            //provider.putObject(config.getS3().getBucketName(), fileObjKeyName, inputStream, metadata);
            provider.putObject(config.getS3().getBucketName(), fileObjKeyName,  Files.write(Paths.get("/tmp/" + "sameName.jpg"), outputStream.toByteArray()).toFile());

            
            //Estado de registro 
            LOGGER.info("Put object in S3");

            //Contruir el response
            /*response.setStatusCode(200);
            Map<String, String> responseBody = new HashMap<String, String>();
            responseBody.put("Status", "File stored in S3");
            String responseBodyString = new JSONObject(responseBody).toJSONString();
            response.setBody(responseBodyString);
            
            return responseBodyString;*/
            return new Response();

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
