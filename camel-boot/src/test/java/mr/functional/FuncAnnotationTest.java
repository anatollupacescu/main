package mr.functional;

import static org.junit.Assert.assertEquals;

import java.util.function.Function;

import org.junit.Test;

public class FuncAnnotationTest {

	@Test
	public void test1() {
		FuncContext context = FuncRunner.<FuncAnnotationTest> run(new FuncAnnotationTest());
		assertEquals("testValue", context.get("testValueF"));
	}

	@Test
	public void test2() {
		FuncContext context = FuncRunner.<FuncAnnotationTest> run(new FuncAnnotationTest());
		assertEquals("hi testValue", context.get("concat1"));
	}

	@Test
	public void test3() {
		FuncContext context = FuncRunner.<FuncAnnotationTest> run(new FuncAnnotationTest());
		assertEquals("testValue-testValue", context.get("concat2"));
	}

	@FuncAnnotation
	private final Function<FuncContext, Object> testValueF = new Function<FuncContext, Object>() {
		@Override
		public Object apply(FuncContext t) {
			return "testValue";
		}
	};

	@FuncAnnotation
	private final Function<FuncContext, Object> concat1 = new Function<FuncContext, Object>() {
		@Override
		public Object apply(FuncContext t) {
			return "hi " + t.get("testValueF");
		}
	};

	@FuncAnnotation
	private final Function<FuncContext, Object> concat2 = new Function<FuncContext, Object>() {
		@Override
		public Object apply(FuncContext t) {
			return String.format("%s-%s", (String) t.get("testValueF"), (String) t.get("testValueF"));
		}
	};
}
