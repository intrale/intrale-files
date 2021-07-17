package ar.com.intrale.cloud;

import java.util.Map;

import javax.inject.Singleton;

import ar.com.intrale.cloud.exceptions.FunctionException;

@Singleton
public class DownloadStringToRequestBuilder extends StringToRequestBuilder<Request> {

	@Override
	public Request build(Map<String, String> headers, Map<String, String> pathParameters, String source)
			throws FunctionException {
		Request request = new Request();
		request.setHeaders(headers);
		request.setPathParameters(pathParameters);
		return request;
	}

}
