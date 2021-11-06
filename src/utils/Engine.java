package utils;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Engine implements Cloneable {
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

	public boolean isConditionSatisfied(String condition) {
		try {
			if (condition.isEmpty() || condition.isBlank()) {
				return true;
			} else {
				return (boolean) ((Object) this.engine.eval(condition));
			}
		} catch (ScriptException e) {
			return false;
		}
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void debug() {
		System.out.println("\n===================== ENGINE =====================\n");

		for (String key : this.engine.getBindings(ScriptContext.ENGINE_SCOPE).keySet()) {
			System.out
					.println("[ " + key + " ] " + this.engine.getBindings(ScriptContext.ENGINE_SCOPE).get(key) + "\n");
		}

		System.out.println("\n==================================================\n");
	}
}
