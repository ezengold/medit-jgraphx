package ui;

import java.awt.Graphics;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Button extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Button(String TITLE, String ALT_TEXT, String TOOLTIP_TEXT, String ICON_URL) {
		// TODO Auto-generated constructor stub
		setContentAreaFilled(false);
		setToolTipText(TITLE);
		URL btnIconUrl = getClass().getResource(ICON_URL);
		if (btnIconUrl != null) {
			setIcon(new ImageIcon(btnIconUrl, ALT_TEXT));
		} else {
			System.err.println("Resource not found: " + ICON_URL);
			setText(TITLE);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
	}
}
