package utils;

import models.Notification;

public interface Observable {
	// add an observer to the list of observers
	public void addObserver(Observer observer);

	// updates all the obervers
	public void updateObservers();

	// updates all the obervers
	public void updateObservers(Notification notification);

	// remove all oberservers
	public void removeObserver();
}
