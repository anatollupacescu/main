package mr.functional;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Maps;

/**
 * Created by anatol on 11/16/14.
 */
public class FuncContext {

	private final Map<String, SFunction> vars = Maps.newHashMap();

	public static FuncContext newContext() {
		return new FuncContext();
	}

	private void put(String key, SFunction func) {
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
		vars.put(varName, new SFunction(function));
		return this;
	}

	public FuncContext register(String varName, Function<FuncContext, Object> function, String[] strings) {
		SFunction f = new SFunction(function);
		FuncContext localContext = view(strings);
		f.setLocalContext(localContext);
		vars.put(varName, f);
		return this;
	}

	public Object get(String string) {
		SFunction func = vars.get(string);
		if (func == null) {
			throw new IllegalStateException(string + " not registred");
		}
		return func.apply(this);
	}

	@SuppressWarnings("unchecked")
	public <V> V get(String string, Class<V> classType) {
		SFunction func = vars.get(string);
		if (func == null) {
			throw new IllegalStateException(string + " not registred");
		}
		return (V) func.apply(this);
	}

	public SFunction wrapFunc(Function<FuncContext, Object> func) {
		SFunction f = new SFunction(func);
		f.setLocalContext(this);
		return f;
	}

	public static Function<FuncContext, Object> ready(Object obj) {
		return new SFunction(obj);
	}

	private static final class SFunction implements Function<FuncContext, Object> {

		enum NOTHING {
			$
		}

		private final Function<FuncContext, Object> function;
		private Object cached;
		private FuncContext localContext;

		public SFunction(Function<FuncContext, Object> func) {
			this.function = func;
		}

		public SFunction(Object ready) {
			this.cached = ready;
			this.function = null;
		}

		@Override
		public Object apply(FuncContext sContext) {

			if (cached == NOTHING.$) {
				return null;
			}

			FuncContext context = localContext;

			if (context == null) {
				context = sContext;
			}

			if (cached == null) {
				cached = function.apply(context);
			}

			if (cached == null) {
				cached = NOTHING.$;
				return null;
			}

			return cached;
		}

		public void setLocalContext(FuncContext localContext) {
			this.localContext = localContext;
		}
	}
}
