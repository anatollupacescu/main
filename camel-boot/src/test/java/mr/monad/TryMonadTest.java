package mr.monad;

import static org.junit.Assert.assertTrue;

import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Test;

public class TryMonadTest {

	@Test
	public void testM0() {

		test(Try.success("jora1"));
	}

	@Test
	public void testM1() {

		test(Try.success("jora"));
	}

	private <T> void test(Try<String> monad) {

		Try<RuntimeException> e = monad

		.map(new Function<String, Integer>() {
			@Override
			public Integer apply(String t) {
				return t.length();
			}
		})

		.map(new Function<Integer, String>() {
			@Override
			public String apply(Integer t) {
				if (t > 4) {
					return "E ok";
				}
				throw new RuntimeException("Too short");
			}
		})

		.ifPresentOrFail(new Consumer<String>() {
			@Override
			public void accept(String t) {
				assertTrue("E ok".equals(t));
			}
		})

		;

		e.ifPresent(new Consumer<RuntimeException>() {
			@Override
			public void accept(RuntimeException t) {
				assertTrue(t instanceof IllegalStateException);
				assertTrue(t.getCause() instanceof RuntimeException);
			}
		});
	}
}
