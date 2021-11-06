package models;

import java.util.UUID;

import utils.Engine;

public class Trace {

	private String traceId;

	private State currentState;

	private State targetState;

	private Transition currentTransition;

	protected Engine engine;

	public Trace(Transition currenTransition) {
		this.traceId = UUID.randomUUID().toString();
		this.currentTransition = (Transition) currenTransition.clone();
	}

	public Trace(Transition currenTransition, State currentState, State targetState) {
		this.traceId = UUID.randomUUID().toString();
		this.currentTransition = (Transition) currenTransition.clone();
		this.currentState = (State) currentState.clone();
		this.targetState = (State) targetState.clone();
	}

	public Trace(Transition currenTransition, State currentState, State targetState, Engine engine) {
		this.traceId = UUID.randomUUID().toString();
		this.currentTransition = (Transition) currenTransition.clone();
		this.currentState = (State) currentState.clone();
		this.targetState = (State) targetState.clone();
		this.engine = (Engine) engine.clone();
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(State currentState) {
		this.currentState = (State) currentState.clone();
	}

	public State getTargetState() {
		return targetState;
	}

	public void setTargetState(State targetState) {
		this.targetState = (State) targetState.clone();
	}

	public Transition getCurrentTransition() {
		return currentTransition;
	}

	public void setCurrentTransition(Transition currentTransition) {
		this.currentTransition = (Transition) currentTransition.clone();
	}

	public Engine getEngine() {
		return engine;
	}

	public void setEngine(Engine engine) {
		this.engine = (Engine) engine.clone();
	}
}
