package mr.nashorn;

import java.util.HashMap;
import java.util.Map;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

public class Javascript {
	
	final String statement = "function fetch(values) { values['map'].put('res', 'test'); return values['map'].get('first_name'); };";
	final FullName fullName ;
	final SimpleBindings bindings;
	final Map<String, Object> map = new HashMap<String, Object>();
	
	public Javascript() {
		final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		final Compilable compilable = (Compilable) engine;
		final Invocable invocable = (Invocable) engine;
		CompiledScript compiled;
		try {
			compiled = compilable.compile(statement);
			compiled.eval();
		} catch (ScriptException e) {
			throw new RuntimeException("Could not initiate task", e);
		}
		fullName = invocable.getInterface(FullName.class);
		bindings = new SimpleBindings();
		bindings.put("map", map);
	}

	public static void main1(String[] args) throws Exception {
		Javascript demo = new Javascript();
		String result = demo.execute("Joel");
		System.out.println("full name is " + result);
	}

	public String execute(String name) throws ScriptException, NoSuchMethodException {
		map.put("first_name", name);
		String fn = fullName.fetch(bindings);
		System.out.println(map.get("res"));
		return fn;
	}

	public interface FullName {
		String fetch(SimpleBindings values);
	}
}
