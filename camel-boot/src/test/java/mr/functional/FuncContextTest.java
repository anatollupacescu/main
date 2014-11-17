package mr.functional;

import static org.junit.Assert.*;

import java.util.function.Function;

import org.junit.Test;

public class FuncContextTest {

	@Test
	public void test1() {
		FuncContext context = FuncContext.newContext();
		context.register("test", testValueF);
		assertEquals("testValue", context.get("test"));
	}

	@Test
	public void test2() {
		FuncContext context = FuncContext.newContext();
		context.register("test", testValueF);
		context.register("echo", concatF);
		assertEquals("hi testValue", context.get("echo"));
	}

	@Test(expected=IllegalStateException.class)
	public void test3() {
		FuncContext context = FuncContext.newContext();
		context.register("test", testValueF);
		context.register("echo", concatF, new String[] {});
		assertEquals("hi testValue", context.get("echo"));
	}

	@Test
	public void test4() {
		FuncContext context = FuncContext.newContext();
		context.register("test", testValueF);
		context.register("echo", concatF, new String[] { "test" });
		assertEquals("hi testValue", context.get("echo"));
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
}
