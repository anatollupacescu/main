package mr.functional;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * Created by anatol on 11/16/14.
 */
public class FuncContext {

	private static final ExecutorService executor = Executors.newCachedThreadPool();

	private final Map<String, FuncCallable> callableMap = new WeakHashMap<String, FuncCallable>();
	private final Map<String, Future<Object>> futureMap = new WeakHashMap<String, Future<Object>>();
	private final Map<String, Object> readyMap = new WeakHashMap<String, Object>();

	private void put(String key, Future<Object> func) {
		futureMap.put(key, func);
	}

	private FuncContext view(String... strings) {
		FuncContext local = new FuncContext();
		for (String key : strings) {
			local.put(key, futureMap.get(key));
		}
		return local;
	}

	public FuncContext register(String varName, Function<FuncContext, Object> function) {
		FuncCallable callable = new FuncCallable(this, function);
		callableMap.put(varName, callable);
		return this;
	}

	public FuncContext register(String varName, Function<FuncContext, Object> function, String[] filter) {
		FuncContext localContext = view(filter);
		FuncCallable callable = new FuncCallable(localContext, function);
		callableMap.put(varName, callable);
		return this;
	}

	public void run() {
		Set<Entry<String, FuncCallable>> entrySet = callableMap.entrySet();
		CountDownLatch latch = new CountDownLatch(1);
		for (Entry<String, FuncCallable> entry : entrySet) {
			String name = entry.getKey();
			FuncCallable value = entry.getValue();
			value.setLatch(latch);
			futureMap.put(name, executor.submit(value));
		}
		latch.countDown();
	}

	public void run(Object... args) {
		Object key = null;
		for (final Object object : args) {
			if (key == null) {
				key = object;
			} else {
				readyMap.put((String) key, object);
			}
		}
		run();
	}

	public Object get(String string) throws RuntimeException {
		Object ready = readyMap.get(string);
		if (ready != null) {
			return ready;
		}
		Future<Object> func = futureMap.get(string);
		if (func == null) {
			throw new IllegalStateException(string + " not registred");
		}
		try {
			return func.get();
		} catch (InterruptedException | ExecutionException e) {
			throw (RuntimeException) e.getCause();
		}
	}

	@SuppressWarnings("unchecked")
	public <V> V get(String string, Class<V> classType) throws RuntimeException {
		return (V) get(string);
	}

	private final static class FuncCallable implements Callable<Object> {

		private final Function<FuncContext, Object> function;
		private final FuncContext context;
		private CountDownLatch latch;

		public FuncCallable(FuncContext localContext, Function<FuncContext, Object> func) {
			this.context = localContext;
			this.function = func;
		}

		public void setLatch(CountDownLatch latch) {
			this.latch = latch;
		}

		@Override
		public Object call() throws Exception {
			latch.await();
			return function.apply(context);
		}
	}

	private static final String FUNC_NOT_FOUND_MESSAGE = "Field '%s' must be an instance of Function";

	public static FuncContext newContext() {
		return new FuncContext();
	}

	@SuppressWarnings("unchecked")
	public static <T> FuncContext build(T test) {
		final FuncContext context = new FuncContext();
		Class<?> klass = test.getClass();
		for (Field field : klass.getDeclaredFields()) {
			FuncAnnotation annotation = field.getAnnotation(FuncAnnotation.class);
			if (annotation != null) {
				Class<?> fieldType = field.getType();
				if (!Function.class.equals(fieldType)) {
					throw new IllegalArgumentException(String.format(FUNC_NOT_FOUND_MESSAGE, field.getName()));
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
