package models;

import java.util.UUID;

public class State {
	private static Integer NB = 0;

	private String stateId;
	
	private String name;

	private String invariant = "";

	private boolean isInitial = false;

	public State() {
		this.name = "s" + NB.toString();
		NB++;
		this.setStateId(UUID.randomUUID().toString());
	}

	public State(String name) {
		this.name = name;
		NB++;
		this.setStateId(UUID.randomUUID().toString());
	}

	public String getStateId() {
		return stateId;
	}

	public void setStateId(String stateId) {
		this.stateId = stateId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInvariant() {
		return invariant;
	}

	public void setInvariant(String invariant) {
		this.invariant = invariant;
	}

	public boolean isInitial() {
		return isInitial;
	}

	public void setInitial(boolean isInitial) {
		this.isInitial = isInitial;
	}

	@Override
	public String toString() {
		return name;
	}
}
