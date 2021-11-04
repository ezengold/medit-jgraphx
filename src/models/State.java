package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class State implements Serializable {

	private static final long serialVersionUID = -1569774286174664135L;

	public static Integer NB = 0;

	private String stateId;

	private String name;

	private String invariant = "";

	private boolean isInitial = false;

	private ArrayList<String> propertiesVerified = new ArrayList<>();

	private Position position;

	public State() {
		this.name = "s" + NB.toString();
		addProperty(this.name);
		NB++;
		this.setStateId(UUID.randomUUID().toString());
		this.setPosition(0, 0);
	}

	public State(String name) {
		this.name = name;
		addProperty(name);
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
		if (!isPropertySatisfy(name)) {
			removeProperty(this.name);
			addProperty(name);
		}
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

	public void setPosition(double x, double y) {
		if (this.position != null) {
			this.position.setX(x);
			this.position.setY(y);
		} else {
			this.position = new Position(x, y);
		}
	}


	public void addProperty(String property) {
		if (!isPropertySatisfy(property)) {
			this.propertiesVerified.add(property);
		}
	}

	public void removeProperty(String property) {
		this.propertiesVerified.remove(property);
	}

	public boolean isPropertySatisfy(String property) {
		return this.propertiesVerified.contains(property);
	}


	public String debug() {
		String verb = "";
		verb += "StateId ="+ this.getStateId()+" || State Name = "+this.getName()+" || Is initial = "+this.isInitial()+
		" || Position X = "+this.getPosition().getX()+" || Position Y = "+this.getPosition().getY()+ "\n";
		return verb;
	}


	@Override
	public String toString() {
		return name;
	}
}
