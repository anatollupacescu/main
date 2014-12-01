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

	private final ExecutorService executor;

	private final Map<String, FuncCallable> callableMap = new WeakHashMap<String, FuncCallable>();

	private Map<String, ReadyOrFuture> resultMap;

	private FuncContext(ExecutorService service) {
		if (service == null) {
			this.executor = Executors.newCachedThreadPool();
		} else {
			this.executor = service;
		}
	}

	private void initResultMap() {
		resultMap = new WeakHashMap<String, ReadyOrFuture>();
	}

	private void put(String key, ReadyOrFuture obj) {
		resultMap.put(key, obj);
	}

	private FuncContext view(String... strings) {
		FuncContext local = cloneContext();
		local.initResultMap();
		for (String key : strings) {
			ReadyOrFuture mappedValue = resultMap.get(key);
			local.put(key, mappedValue);
		}
		return local;
	}

	public FuncContext register(String varName, Function<FuncContext, Object> function) {
		return register(varName, function, null);
	}

	public FuncContext register(String varName, Function<FuncContext, Object> function, String[] filter) {
		FuncCallable callable = new FuncCallable(this, function, filter);
		callableMap.put(varName, callable);
		return this;
	}

	public FuncContext run() {
		return run(new Object[] {});
	}

	public FuncContext run(Object... args) {
		initResultMap();
		Object key = null;
		for (final Object object : args) {
			if (key == null) {
				key = object;
			} else {
				resultMap.put((String) key, new ReadyOrFuture(object));
				key = null;
			}
		}
		Set<Entry<String, FuncCallable>> entrySet = callableMap.entrySet();
		CountDownLatch latch = new CountDownLatch(1);
		for (Entry<String, FuncCallable> entry : entrySet) {
			String name = entry.getKey();
			FuncCallable value = entry.getValue();
			value.setLatch(latch);
			resultMap.put(name, new ReadyOrFuture(executor.submit(value)));
		}
		latch.countDown();
		return this;
	}

	public Object get(String string) {
		ReadyOrFuture r = resultMap.get(string);
		if (r == null) {
			throw new IllegalStateException(string + " not registred");
		}
		try {
			return r.get();
		} catch (Exception e) {
			throw (RuntimeException) e.getCause();
		}
	}

	@SuppressWarnings("unchecked")
	public <V> V get(String string, Class<V> classType) throws RuntimeException {
		return (V) get(string);
	}

	private final static class FuncCallable implements Callable<Object> {

		private final Function<FuncContext, Object> function;

		private FuncContext context;
		private CountDownLatch latch;
		private String[] filter;

		public FuncCallable(FuncContext localContext, Function<FuncContext, Object> func, String[] filter) {
			this.context = localContext;
			this.function = func;
			this.filter = filter;
		}

		public void setLatch(CountDownLatch latch) {
			this.latch = latch;
		}

		@Override
		public Object call() throws Exception {
			latch.await();
			if (filter != null) {
				this.context = context.view(filter);
			}
			return function.apply(context);
		}
	}

	private final static class ReadyOrFuture {
		private final Object ready;
		private final Future<Object> future;

		public ReadyOrFuture(Object obj) {
			this.ready = obj;
			this.future = null;
		}

		public ReadyOrFuture(Future<Object> future) {
			this.ready = null;
			this.future = future;
		}

		public Object get() throws InterruptedException, ExecutionException {
			if (ready != null)
				return ready;
			return future.get();
		}
	}

	private static final String FUNC_NOT_FOUND_MESSAGE = "Field '%s' must be an instance of Function";

	public static FuncContext newContext() {
		return new FuncContext(null);
	}

	public static FuncContext newContext(ExecutorService service) {
		return new FuncContext(service);
	}

	private FuncContext cloneContext() {
		return new FuncContext(executor);
	}

	@SuppressWarnings("unchecked")
	public static <T> FuncContext build(T test) {
		final FuncContext context = FuncContext.newContext();
		Class<?> klass = test.getClass();
		for (Field field : klass.getDeclaredFields()) {
			FuncAnnotation annotation = field.getAnnotation(FuncAnnotation.class);
			if (annotation != null) {
				/* check type */
				if (!Function.class.equals(field.getType())) {
					throw new IllegalArgumentException(String.format(FUNC_NOT_FOUND_MESSAGE, field.getName()));
				}
				field.setAccessible(true);
				/* function body */
				Function<FuncContext, Object> function = null;
				try {
					function = (Function<FuncContext, Object>) field.get(test);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				/* name */
				final String name;
				if (annotation.name().length() > 0) {
					name = annotation.name();
				} else {
					name = field.getName();
				}
				/* register */
				if (annotation.mappedVars().length > 0) {
					context.register(name, function, annotation.mappedVars());
				} else {
					context.register(name, function);
				}
			}
		}
		return context;
	}
}
