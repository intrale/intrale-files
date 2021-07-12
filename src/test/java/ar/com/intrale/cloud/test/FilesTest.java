package ar.com.intrale.cloud.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import ar.com.intrale.cloud.IntraleFactory;
import ar.com.intrale.cloud.LambdaStream;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.test.annotation.MicronautTest;

@MicronautTest(rebuildContext = true)
@Property(name = IntraleFactory.FACTORY, value = "true")
@Property(name = IntraleFactory.PROVIDER, value = "true")
public class FilesTest extends ar.com.intrale.cloud.Test {
	
	private LambdaStream lambdaStream;

	@Override
	public void beforeEach() {
	}

	@Override
	public void afterEach() {
	}
	
	@Override
	public void lambdaInstantiation() {
 		if (lambdaStream == null) {
			BeanIntrospection<LambdaStream> beanIntrospection = BeanIntrospection.getIntrospection(LambdaStream.class);
			lambdaStream = beanIntrospection.instantiate();
		}
	}

	@Test
	public void test() {
		try {
			lambdaStream.handleRequest(new ByteArrayInputStream("prueba".getBytes()), new ByteArrayOutputStream(), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
