package models;

public class State {
	private static Integer NB = 1;

	private String name;

	private String invariant = "";

	private boolean isInitial = false;

	public State() {
		this.name = "s" + NB.toString();
		NB++;
	}

	public State(String name) {
		this.name = name;
		NB++;
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
