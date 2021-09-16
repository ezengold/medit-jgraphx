package models;

import java.util.UUID;

import org.jgrapht.graph.DefaultEdge;

public class Transition extends DefaultEdge {

	private static final long serialVersionUID = 7209157154944409893L;

	private String transitionId;

	private String sourceId;

	private String targetId;

	private String guard = "";

	private String update = "";

	public Transition() {
		this.transitionId = UUID.randomUUID().toString();
	}

	public Transition(String sourceId, String targetId) {
		this.transitionId = UUID.randomUUID().toString();
		this.setSourceId(sourceId);
		this.setTargetId(targetId);
	}

	public String getTransitionId() {
		return transitionId;
	}

	public void setTransitionId(String transitionId) {
		this.transitionId = transitionId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
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
