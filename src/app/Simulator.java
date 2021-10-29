package app;

import javax.swing.JPanel;

public class Simulator extends JPanel {

	private static final long serialVersionUID = 8364570541969774335L;

	private App app;
	
	public Simulator(App app) {
		this.app = app;
	}

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}
}
