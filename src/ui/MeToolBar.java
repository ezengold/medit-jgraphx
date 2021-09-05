package ui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JToolBar;

import app.App;
import utils.EditorActions.*;

public class MeToolBar extends JToolBar {
	
	private static final long serialVersionUID = -2507584235981111199L;

	public MeToolBar(final App app, int orientation) {
		super(orientation);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3), getBorder()));
		setFloatable(false);
		
		add(app.bind("Nouveau", new NewAction(), "/file.png"));
		add(app.bind("Ouvrir", new OpenAction(), "/folder.png"));
		add(app.bind("Enregistrer", new SaveAction(), "/disk.png"));
		addSeparator(new Dimension(30, 0));
		add(app.bind("Sélecteur", new CursorAction(), "/cursor.png"));
		add(app.bind("État", new StateAction(), "/location.png"));
		add(app.bind("Transition", new EdgeAction(), "/edge.png"));
	}
}
