package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class Console extends JPanel {

	private static final long serialVersionUID = -3520320175289723709L;

	protected JTextArea content;
	protected boolean isError = false;

	public Console() {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(5, 0, 0, 5));

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(true);
		header.setBackground(new Color(0, 0, 0, 20));
		header.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel title = new JLabel("Console");
		title.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
		header.add(title, BorderLayout.WEST);

		JButton clearBtn = new JButton(new ImageIcon(getClass().getResource("/clear.png")));
		clearBtn.setOpaque(true);
		clearBtn.setBorderPainted(false);
		clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearConsole();
			}
		});
		header.add(clearBtn, BorderLayout.EAST);

		add(header, BorderLayout.NORTH);

		content = new JTextArea("Aucune erreur");
		content.setEditable(false);
		content.setForeground(isError ? Color.RED : Color.decode("#9099ae"));
		content.setLineWrap(true);
		content.setWrapStyleWord(true);
		content.setBorder(new EmptyBorder(10, 10, 10, 10));
		content.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
		content.setAlignmentY(SwingConstants.TOP);

		JScrollPane scrollContent = new JScrollPane(content);
		scrollContent.setBorder(new EmptyBorder(5, 0, 0, 0));
		scrollContent.setOpaque(true);
		scrollContent.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);
		scrollContent.setAlignmentY(JScrollPane.TOP_ALIGNMENT);

		add(scrollContent, BorderLayout.CENTER);
	}

	public void error(String message) {
		isError = true;
		content.setForeground(Color.RED);
		content.setText(content.getText() + message);
	}

	public void printError(String message) {
		isError = true;
		content.setForeground(Color.RED);
		content.setText(message);
	}

	public void success(String message) {
		isError = false;
		content.setForeground(Color.decode("#9099ae"));
		content.setText(content.getText() + message);
	}

	public void printSuccess(String message) {
		isError = false;
		content.setForeground(Color.decode("#9099ae"));
		content.setText(message);
	}

	public void clearConsole() {
		isError = false;
		content.setForeground(Color.decode("#9099ae"));
		content.setText("");
	}
}
