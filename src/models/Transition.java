package models;

import java.io.Serializable;
import java.util.UUID;

import org.jgrapht.graph.DefaultEdge;

public class Transition extends DefaultEdge implements Serializable {
	
	private static final long serialVersionUID = 4467909557424652342L;

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

	public String getGuard() {
		return guard;
	}

	public void setGuard(String guard) {
		this.guard = guard;
	}

	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}
	
	@Override
	public String toString() {
		return guard;
	}
}
