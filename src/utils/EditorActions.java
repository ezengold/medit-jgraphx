package utils;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import app.App;

public class EditorActions {

	public static final App getApp(ActionEvent e) {
		if (e.getSource() instanceof Component) {
			Component component = (Component) e.getSource();

			while (component != null && !(component instanceof App)) {
				component = component.getParent();
			}

			return (App) component;
		}

		return null;
	}

	@SuppressWarnings("serial")
	public static class NewAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				System.out.println("NEW FILE CLICKED");
			}
		}

	}

	@SuppressWarnings("serial")
	public static class OpenAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				System.out.println("OPEN FILE CLICKED");
			}
		}

	}

	@SuppressWarnings("serial")
	public static class SaveAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				System.out.println("SAVE FILE CLICKED");
			}
		}

	}

	@SuppressWarnings("serial")
	public static class CloseAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				System.out.println("CLOSE CLICKED");
			}
		}

	}

	@SuppressWarnings("serial")
	public static class QuitAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				System.out.println("QUIT CLICKED");
			}
		}

	}

	@SuppressWarnings("serial")
	public static class SimulatorAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				System.out.println("SIMULATOR CLICKED");
			}
		}

	}

	@SuppressWarnings("serial")
	public static class AboutAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				System.out.println("ABOUT CLICKED");
			}
		}

	}

	@SuppressWarnings("serial")
	public static class CursorAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				System.out.println("CURSOR CLICKED");
			}
		}

	}

	@SuppressWarnings("serial")
	public static class StateAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				System.out.println("STATE CLICKED");
			}
		}

	}

	@SuppressWarnings("serial")
	public static class EdgeAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				System.out.println("EDGE CLICKED");
			}
		}

	}
}
