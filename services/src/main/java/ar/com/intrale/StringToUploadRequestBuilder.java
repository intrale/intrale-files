package ar.com.intrale;

import java.util.Map;

import javax.inject.Singleton;

import ar.com.intrale.Builder;
import ar.com.intrale.exceptions.FunctionException;

@Singleton
public class StringToUploadRequestBuilder /*extends StringToRequestBuilder<UploadRequest>*/ implements Builder<String, UploadRequest> {

	@Override
	public UploadRequest build(Map<String, String> headers, Map <String, String> pathParameters, String source) throws FunctionException {
		UploadRequest request = new UploadRequest();
		request.setHeaders(headers);
		request.setPathParameters(pathParameters);
		request.setContent(source);
		return request;
	}

}
