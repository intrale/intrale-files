package ar.com.intrale.cloud;

import java.util.Map;

import javax.inject.Singleton;

import ar.com.intrale.cloud.exceptions.FunctionException;

@Singleton
public class StringToUploadRequestBuilder /*extends StringToRequestBuilder<UploadRequest>*/ implements Builder<String, UploadRequest> {

	@Override
	public UploadRequest build(Map<String, String> headers, Map <String, String> queryStringParameters, String source) throws FunctionException {
		UploadRequest request = new UploadRequest();
		request.setHeaders(headers);
		request.setContent(source);
		return request;
	}

}
