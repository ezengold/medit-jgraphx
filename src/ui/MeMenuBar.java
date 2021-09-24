package ui;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import app.App;
import utils.EditorActions.*;

public class MeMenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MeMenuBar(final App app) {
		JMenu menu = null;
		
		// Create file menu
		menu = add(new JMenu("Fichier"));
		menu.add(app.bind("Nouveau", new NewAction()));
		menu.add(app.bind("Ouvrir", new OpenAction()));
		menu.add(app.bind("Sauvegarder", new SaveAction()));
		menu.addSeparator();
		menu.add(app.bind("Fermer", new CloseAction()));
		
		menu = add(new JMenu("Aide"));
		menu.add(app.bind("A propos", new AboutAction()));
	}
}
