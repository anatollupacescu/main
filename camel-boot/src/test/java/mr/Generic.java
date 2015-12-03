package mr;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by anatolie.lupacescu on 25/11/2015.
 */
public class Generic {

	@Test
	public void test() {
		String var = "test";
		String defaultValue = "default";
		String uri = Optional.ofNullable(var).orElseGet(() -> defaultValue);
		Assert.assertEquals(uri, var);
	}
}
