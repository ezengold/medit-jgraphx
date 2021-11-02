package utils;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Engine {
	private ScriptEngineManager manager;
	private ScriptEngine engine;

	public Engine() {
		this.manager = new ScriptEngineManager();
		this.engine = manager.getEngineByName("JavaScript");
	}

	public void setVariable(String name, Object value) {
		engine.put(name, value);
	}

	public void removeVariable(String name) {
		engine.getBindings(ScriptContext.ENGINE_SCOPE).remove(name);
	}

	public void resetEngine() {
		this.manager = new ScriptEngineManager();
		this.engine = this.manager.getEngineByName("JavaScript");
	}

	public boolean evaluateCondition(String condition) {
		try {
			return (boolean) this.engine.eval(condition);
		} catch (ScriptException e) {
			return false;
		}
	}
}
