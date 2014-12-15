package mr.functional;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class MethodsTest {

	@Test
	public void test() {
		MethodsContext mc = new MethodsContext(new Methods());
		Object fullName = mc.get( "fullName", ImmutableMap.of("name", "Jora" ));
		assertEquals( "Mr. Jora", fullName);
	}

}
