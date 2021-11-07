package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Button extends JButton {

	private static final long serialVersionUID = 8366435826897373230L;

	private Color oldForgroundColor = Color.decode("#666666"), oldBackgroundColor = new Color(0, 0, 0, 60);

	public Button() {
		super();
		setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
		setBorder(new EmptyBorder(10, 15, 10, 15));
		setOpaque(true);
		setFocusPainted(false);
		setBorderPainted(false);
		setBackground(Color.decode("#dddddd"));
		addFocusListener(buttonFocusListener);
		addMouseListener(buttonMouseListener);
	}

	public Button(String text) {
		super(text);
		setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
		setBorder(new EmptyBorder(10, 15, 10, 15));
		setOpaque(true);
		setFocusPainted(false);
		setBorderPainted(false);
		setBackground(Color.decode("#dddddd"));
		addFocusListener(buttonFocusListener);
		addMouseListener(buttonMouseListener);
	}

	public Button(ImageIcon imageIcon) {
		super(imageIcon);
		setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
		setBorder(new EmptyBorder(10, 15, 10, 15));
		setOpaque(true);
		setFocusPainted(false);
		setBorderPainted(false);
		setBackground(Color.decode("#dddddd"));
		addFocusListener(buttonFocusListener);
		addMouseListener(buttonMouseListener);
	}

	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
	}

	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
	}

	private FocusListener buttonFocusListener = new FocusListener() {

		@Override
		public void focusLost(FocusEvent e) {
			setContentAreaFilled(true);
			setBorderPainted(false);
		}

		@Override
		public void focusGained(FocusEvent e) {
			setContentAreaFilled(false);
			setBorderPainted(true);
			setBorder(new CompoundBorder(new LineBorder(getBackground(), 1), new EmptyBorder(9, 14, 9, 14)));
		}
	};

	private MouseListener buttonMouseListener = new MouseListener() {

		@Override
		public void mouseReleased(MouseEvent e) {
			//
		}

		@Override
		public void mousePressed(MouseEvent e) {
			//
		}

		@Override
		public void mouseExited(MouseEvent e) {
			setContentAreaFilled(true);
			setBorderPainted(false);
			setForeground(getOldForgroundColor());
			setBackground(getOldBackgroundColor());
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			setOldForgroundColor(getForeground());
			setOldBackgroundColor(getBackground());

			setContentAreaFilled(false);
			setBorderPainted(true);
			setForeground(getBackground());
			setBorder(new CompoundBorder(new LineBorder(getBackground(), 1), new EmptyBorder(9, 14, 9, 14)));

			setBackground(Color.decode("#dddddd"));
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			//
		}
	};

	public Color getOldBackgroundColor() {
		return oldBackgroundColor;
	}

	public void setOldBackgroundColor(Color oldBackgroundColor) {
		this.oldBackgroundColor = oldBackgroundColor;
	}

	public Color getOldForgroundColor() {
		return oldForgroundColor;
	}

	public void setOldForgroundColor(Color oldForgroundColor) {
		this.oldForgroundColor = oldForgroundColor;
	}
}
