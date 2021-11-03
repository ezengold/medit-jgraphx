package utils;

public interface Observable {
	// add an observer to the list of observers
	public void addObserver(Observer observer);

	// updates all the obervers
	public void updateObservers();

	// remove all oberservers
	public void removeObserver();
}
