package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import models.State;

public class ConfigStateDialog extends JDialog {

	private static final long serialVersionUID = -1659747112795946693L;

	protected String label = "";
	protected JTextField labelField = new JTextField();

	protected String invariant = "";
	protected JTextField invariantField = new JTextField();

	protected boolean isInitial = false;
	protected JCheckBox isInitialBox = new JCheckBox();

	public ConfigStateDialog(State state) {
		super((Frame) null, state.getName(), true);
		setModal(true);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dialogSize = getSize();
		setLocation(screenSize.width / 2 - (dialogSize.width / 2), screenSize.height / 2 - (dialogSize.height / 2));

		JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
		panel.add(new JLabel("Nom de l'état : "));
		labelField.setText(state.getName());
		panel.add(labelField);

		panel.add(new JLabel("Invariant : "));
		invariantField.setText(state.getInvariant());
		panel.add(invariantField);

		isInitialBox = new JCheckBox("Initial", state.isInitial());
		panel.add(isInitialBox);

		JPanel buttonsPanel = new JPanel();

		JButton submitButton = new JButton("Valider");
		submitButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		submitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("SUBMIT CLICKED");
				if (applyChanges()) {
					setVisible(false);
				}
			}
		});

		JButton cancelButton = new JButton("Annuler");
		cancelButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		buttonsPanel.add(cancelButton);
		buttonsPanel.add(submitButton);

		JPanel container = new JPanel(new BorderLayout());
		container.setBorder(new EmptyBorder(15, 15, 15, 15));
		container.add(panel, BorderLayout.CENTER);
		container.add(buttonsPanel, BorderLayout.SOUTH);

		getContentPane().add(container);
		pack();
		setResizable(false);
	}
	
	public boolean applyChanges() {
		return true;
	}
}
