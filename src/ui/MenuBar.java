package ui;

import java.awt.Font;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import app.App;
import utils.EditorActions.*;

public class MenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;

	public MenuBar(final App app) {
		JMenu menu = null;
		
		// Create file menu
		JMenu fichierMenu = new JMenu("Fichier");
		fichierMenu.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
		menu = add(fichierMenu);
		// menu.add(app.bind("Nouveau", new NewAction(), "/file.png"));
		menu.add(app.bind("Ouvrir", new OpenAction(), "/folder.png"));
		menu.add(app.bind("Sauvegarder", new SaveAction(), "/disk.png"));
		menu.addSeparator();
		menu.add(app.bind("Fermer", new CloseAction(), "/exit.png"));
		
		JMenu helpMenu = new JMenu("Aide");
		helpMenu.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
		
		menu = add(helpMenu);
		menu.add(app.bind("A propos", new AboutAction(), "/about.png"));
	}
}
