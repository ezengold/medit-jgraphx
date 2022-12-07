package models;

public class Notification {
	public enum Name {
		STATUS_MESSAGE,
		GRAPH_MODIFIED,
		SYSTEM_LINE_CLICKED
	}

	public Name name;

	public Object object;

	public Notification(Name name, Object object) {
		this.name = name;
		this.object = object;
	}
}
