package ar.com.intrale.cloud;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestStreamHandler;

@Introspected
public class LambdaStream extends MicronautRequestStreamHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseFunction.class);
	
	@Override
	protected String resolveFunctionName(Environment env) {
		return "files";
	}

}
