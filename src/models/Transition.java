package models;

import java.util.UUID;

import org.jgrapht.graph.DefaultEdge;

public class Transition extends DefaultEdge {

	private static final long serialVersionUID = 7209157154944409893L;

	private String transitionId;

	private String sourceStateId;

	private String targetStateId;

	private String guard = "";

	private String update = "";

	public Transition() {
		this.transitionId = UUID.randomUUID().toString();
	}

	public Transition(String sourceStateId, String targetStateId) {
		this.transitionId = UUID.randomUUID().toString();
		this.sourceStateId = sourceStateId;
		this.targetStateId = targetStateId;
	}

	public String getSourceStateId() {
		return sourceStateId;
	}

	public String getTargetStateId() {
		return targetStateId;
	}

	public void setSourceStateId(String sourceStateId) {
		this.sourceStateId = sourceStateId;
	}

	public void setTargetStateId(String targetStateId) {
		this.targetStateId = targetStateId;
	}

	public String getTransitionId() {
		return transitionId;
	}

	public void setTransitionId(String transitionId) {
		this.transitionId = transitionId;
	}

	public String getGuardInstructions() {
		return guard;
	}

	public void setGuardInstructions(String guard) {
		this.guard = guard;
	}

	public String getUpdateInstructions() {
		return update;
	}

	public void setUpdateInstructions(String update) {
		this.update = update;
	}
	
	@Override
	public String toString() {
		return guard;
	}
}
