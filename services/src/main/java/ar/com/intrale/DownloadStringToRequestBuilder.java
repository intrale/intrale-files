package ar.com.intrale;

import java.util.Map;

import javax.inject.Singleton;

import ar.com.intrale.exceptions.FunctionException;
import ar.com.intrale.messages.RequestRoot;

@Singleton
public class DownloadStringToRequestBuilder extends StringToRequestBuilder<RequestRoot> {

	@Override
	public RequestRoot build(Map<String, String> headers, Map<String, String> pathParameters, String source)
			throws FunctionException {
		RequestRoot request = new RequestRoot();
		request.setHeaders(headers);
		request.setPathParameters(pathParameters);
		return request;
	}

}
