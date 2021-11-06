package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;

public class State implements Serializable {

	private static final long serialVersionUID = -1569774286174664135L;

	public static Integer NB = 0;

	private String stateId;

	private String name;

	private String invariant = "";

	private boolean isInitial = false;

	//for verifier
	private Hashtable<String,Boolean> propertiesVerified = new Hashtable<>();
	private boolean seenBefore = false;
	private int degrees = 0;


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
		if (!isPropertySatisfy(name)) {
			removeProperty(this.name);
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


	public void addProperty(String property,Boolean value) {
			this.propertiesVerified.put(property,value);
	}

	public void removeProperty(String property) {
		if(isPropertySatisfy(property)) {
			this.propertiesVerified.remove(property);
		}

	}

	public void resetProperties() {
		this.propertiesVerified = new Hashtable<>();
	}

	public Hashtable<String,Boolean> getPropertiesVerified() {
		return propertiesVerified;
	}

	public boolean isPropertySatisfy(String property) {
		if(this.propertiesVerified.get(property) !=null) {
			return this.propertiesVerified.get(property);
		}
		return false;
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

	public boolean isSeenBefore() {
		return seenBefore;
	}

	public void setSeenBefore(boolean seenBefore) {
		this.seenBefore = seenBefore;
	}

	public int getDegrees() {
		return degrees;
	}

	public void setDegrees(int degrees) {
		this.degrees = degrees;
	}
}
