package mr.functional;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.base.Preconditions;

public class MethodsContext {

	private final List<Invokable> ils = new ArrayList<Invokable>();

	private final ExecutorService executor;
	private final Object instance;

	public MethodsContext(Object instance, ExecutorService executor) {
		this.executor = executor;
		this.instance = instance;
		populateInvokableList(instance);
	}

	public MethodsContext(Object instance) {
		this.executor = Executors.newCachedThreadPool();
		this.instance = instance;
		populateInvokableList(instance);
	}

	private void populateInvokableList(Object instance) {
		Class<?> klass = instance.getClass();
		for (Method method : klass.getDeclaredMethods()) {
			FuncAnnotation annotation = method.getAnnotation(FuncAnnotation.class);
			if (annotation != null) {
				MethodCall invoke = new MethodCall(method, annotation);
				ils.add(invoke);
			}
		}
	}

	public Object get(String name, Map<String, Object> input) {
		for (final Entry<String, Object> entry : input.entrySet()) {
			ReadyResult invoke = new ReadyResult(entry.getKey(), entry.getValue());
			ils.add(invoke);
		}
		try {
			Future<?> future = getFuture(name);
			return future.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Future<?> getFuture(String name) throws InterruptedException, ExecutionException {
		Preconditions.checkNotNull(name);
		Iterator<Invokable> iterator = ils.iterator();
		while (iterator.hasNext()) {
			Invokable invoke = iterator.next();
			if (invoke.isNamed(name)) {
				String[] argNames = invoke.getArgumentNames();
				Future<?>[] argValues = new Future<?>[argNames.length];
				int i = 0;
				for (String argName : argNames) {
					argValues[i++] = getFuture(argName);
				}
				return executor.submit(() -> invoke.go(argValues));
			}
		}
		throw new IllegalArgumentException("Could not find method with name " + name);
	}

	private interface Invokable {

		Object go(Future<?>[] args) throws Exception;

		String[] getArgumentNames();

		boolean isNamed(String name);
	}

	private class ReadyResult implements Invokable {

		final Object result;
		final String name;

		public ReadyResult(String name, Object res) {
			this.result = res;
			this.name = name;
		}

		@Override
		public Object go(Future<?>[] ignore) throws Exception {
			return result;
		}

		@Override
		public boolean isNamed(String name) {
			return this.name.equals(name);
		}

		@Override
		public String[] getArgumentNames() {
			return new String[] {};
		}
	}

	private class MethodCall implements Invokable {

		private final Method method;
		private final FuncAnnotation annotation;

		private MethodCall(Method method, FuncAnnotation annotation) {
			super();
			this.method = method;
			this.annotation = annotation;
		}

		@Override
		public boolean isNamed(String name) {
			return annotation.name().equals(name);
		}

		@Override
		public String[] getArgumentNames() {
			return annotation.mappedVars();
		}

		public Object go(Future<?>[] futures) throws Exception {
			try {
				Object[] args = new Object[futures.length];
				int i = 0;
				for (Future<?> future : futures) {
					args[i++] = future.get();
				}
				method.setAccessible(true);
				return method.invoke(instance, args);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
