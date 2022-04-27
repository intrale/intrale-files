package ar.com.intrale;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;

import ar.com.intrale.FunctionBuilder;
import ar.com.intrale.config.ApplicationConfig;
import io.micronaut.function.FunctionBean;
import io.micronaut.http.annotation.Header;

@FunctionBean(name = "files")
public class FilesFunction implements Function<InputStream, OutputStream> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilesFunction.class);
	
	@Inject
	protected ApplicationConfig config;

	@Inject
	private AmazonS3 provider;
	
	@Header(FunctionBuilder.HEADER_AUTHORIZATION) String authorization;
	
	@Override
	public OutputStream apply(InputStream inputStream) {
		LOGGER.info("Prueba invocacion a funcion");
		LOGGER.info("authorization:" + authorization);
		return null;
	}

}
