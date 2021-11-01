package models;

import java.io.Serializable;
import java.util.UUID;

public class State implements Serializable {

	private static final long serialVersionUID = -1569774286174664135L;

	public static Integer NB = 0;

	private String stateId;

	private String name;

	private String invariant = "";

	private boolean isInitial = false;

	private Position position;

	public State() {
		this.name = "s" + NB.toString();
		NB++;
		this.setStateId(UUID.randomUUID().toString());
		this.setPosition(0, 0);
	}

	public State(String name) {
		this.name = name;
		NB++;
		this.setStateId(UUID.randomUUID().toString());
		this.setPosition(0, 0);
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

	public Position getPosition() {
		return position;
	}

	public void setPosition(float x, float y) {
		if (this.position != null) {
			this.position.setX(x);
			this.position.setY(y);
		} else {
			this.position = new Position(x, y);
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
