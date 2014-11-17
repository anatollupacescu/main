package mr.functional;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import com.google.common.collect.Maps;

/**
 * Created by anatol on 11/16/14.
 */
public class FuncContext {

	private static final ExecutorService executor = Executors.newCachedThreadPool();

	private final Map<String, Future<Object>> vars = Maps.newHashMap();

	public static FuncContext newContext() {
		return new FuncContext();
	}

	private void put(String key, Future<Object> func) {
		vars.put(key, func);
	}

	private FuncContext view(String... strings) {
		FuncContext local = new FuncContext();
		for (String key : strings) {
			local.put(key, vars.get(key));
		}
		return local;
	}

	public FuncContext register(String varName, Function<FuncContext, Object> function) {
		FuncCallable callable = new FuncCallable(function);
		callable.setContext(this);
		vars.put(varName, executor.submit(callable));
		return this;
	}

	public FuncContext register(String varName, Function<FuncContext, Object> function, String[] filter) {
		FuncCallable f = new FuncCallable(function);
		FuncContext localContext = view(filter);
		f.setContext(localContext);
		vars.put(varName, executor.submit(f));
		return this;
	}

	public Object get(String string) throws RuntimeException {
		Future<Object> func = vars.get(string);
		if (func == null) {
			throw new IllegalStateException(string + " not registred");
		}
		try {
			return func.get();
		} catch (InterruptedException | ExecutionException e) {
			throw (RuntimeException)e.getCause();
		}
	}

	@SuppressWarnings("unchecked")
	public <V> V get(String string, Class<V> classType) throws RuntimeException {
		return (V) get(string);
	}

	public static Callable<Object> ready(Object obj) {
		return new FuncCallable(obj);
	}
	
	private final static class FuncCallable implements Callable<Object> {

		private final Function<FuncContext, Object> function;
		private Object cached;
		private FuncContext context;

		public FuncCallable(Function<FuncContext, Object> func) {
			this.function = func;
		}

		public FuncCallable(Object ready) {
			this.cached = ready;
			this.function = null;
		}

		@Override
		public Object call() throws Exception {

			if (cached == null) {
				cached = function.apply(context);
			}

			return cached;
		}

		public void setContext(FuncContext localContext) {
			this.context = localContext;
		}
	}
}
