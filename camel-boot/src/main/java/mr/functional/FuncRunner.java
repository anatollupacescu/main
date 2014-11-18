package mr.functional;

import java.lang.reflect.Field;
import java.util.function.Function;

public class FuncRunner<T> {

	@SuppressWarnings("unchecked")
	public static<T> FuncContext run(T test) {
		final FuncContext context = new FuncContext();
		Class<?> klass = test.getClass();
		for (Field field : klass.getDeclaredFields()) {
			FuncAnnotation annotation = field.getAnnotation(FuncAnnotation.class);
			if (annotation != null) {
				Class<?> fieldType = field.getType();
				if(!Function.class.equals(fieldType)) {
					throw new IllegalArgumentException("Field '" + field.getName() + "' must be an instance of Function");
				}
				final String name = field.getName();
				field.setAccessible(true);
				Function<FuncContext, Object> function = null;
				try {
					function = (Function<FuncContext, Object>) field.get(test);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
				context.register(name, function);
			}
		}
		return context;
	}
}
