package mr.functional;

import static org.junit.Assert.assertEquals;

import java.util.function.Function;

import org.junit.Test;

public class FuncContextTest {

	@Test
	public void test1() {
		FuncContext context = FuncContext.newContext();
		context.register("test", testValueF);
		context.run();
		assertEquals("testValue", context.get("test"));
	}

	@Test
	public void test2() {
		FuncContext context = FuncContext.newContext();
		context.register("test", testValueF);
		context.register("echo", concatF);
		context.run();
		assertEquals("hi testValue", context.get("echo"));
	}

	@Test(expected = IllegalStateException.class)
	public void test3() {
		FuncContext context = FuncContext.newContext();
		context.register("test", testValueF);
		context.register("echo", concatF, new String[] {});
		context.run();
		context.get("echo");
	}

	@Test
	public void test4() {
		FuncContext context = FuncContext.newContext();
		context.register("test1", testValueF);
		context.register("test2", testValueF);
		context.register("echo2", concat2F, new String[] { "test1", "test2" });
		context.run();
		assertEquals("testValue-testValue", context.get("echo2"));
	}

	private final Function<FuncContext, Object> testValueF = new Function<FuncContext, Object>() {
		@Override
		public Object apply(FuncContext t) {
			return "testValue";
		}
	};

	private final Function<FuncContext, Object> concatF = new Function<FuncContext, Object>() {
		@Override
		public Object apply(FuncContext t) {
			return "hi " + t.get("test");
		}
	};

	private final Function<FuncContext, Object> concat2F = new Function<FuncContext, Object>() {
		@Override
		public Object apply(FuncContext t) {
			return String.format("%s-%s", (String) t.get("test1"), (String) t.get("test2"));
		}
	};
}
