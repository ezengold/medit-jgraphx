package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;

import models.State;

public class ConfigStateDialog extends JDialog {

	private static final long serialVersionUID = -1659747112795946693L;

	protected mxIGraphModel graphModel;

	private mxCell currentCell;

	protected String label = "";
	protected JTextField labelField = new JTextField();

	protected String invariant = "";
	protected JTextArea invariantField = new JTextArea(10, 10);

	protected boolean isInitial = false;
	protected JCheckBox isInitialBox = new JCheckBox();

	public ConfigStateDialog(final mxCell cell, final mxIGraphModel graphModel) {
		super((Frame) null, "", true);

		State state = (State) cell.getValue();

		setTitle(state.getName());
		this.graphModel = graphModel;
		this.currentCell = cell;

		setModal(true);
		setPreferredSize(new Dimension(300, 300));
		setLocation(500, 200);

		JPanel panel = new JPanel(new GridLayout(5, 1));
		panel.add(new JLabel("Nom de l'état :"));
		labelField.setBorder(new EmptyBorder(5, 5, 5, 5));
		labelField.setText(state.getName());
		panel.add(labelField);

		panel.add(new JLabel("Invariant :"));
		invariantField.setBorder(new EmptyBorder(5, 5, 5, 5));
		invariantField.setLineWrap(true);
		invariantField.setWrapStyleWord(true);
		invariantField.setText(state.getInvariant());
		JScrollPane scrollInvariantField = new JScrollPane(invariantField);
		panel.add(scrollInvariantField);

		isInitialBox = new JCheckBox("Initial", state.isInitial());
		panel.add(isInitialBox);

		JPanel buttonsPanel = new JPanel();

		JButton submitButton = new JButton("Valider");
		submitButton.setBorder(new EmptyBorder(10, 15, 10, 15));
		submitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (applyChanges()) {
					setVisible(false);
				}
			}
		});

		JButton cancelButton = new JButton("Annuler");
		cancelButton.setBorder(new EmptyBorder(10, 15, 10, 15));
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
		if (formIsValid()) {
			State state = (State) currentCell.getValue();

			state.setName(labelField.getText());
			state.setInvariant(invariantField.getText());
			state.setInitial(isInitialBox.isSelected());

			if (state.isInitial()) {
				graphModel.setStyle(currentCell, "fillColor=#888888;strokeColor=#dddddd");
			} else {
				graphModel.setStyle(currentCell, "");
			}

			graphModel.setValue(currentCell, state);

			return true;
		} else {
			return false;
		}
	}

	public boolean formIsValid() {
		if (labelField.getText() != null && labelField.getText() != "" && invariantField.getText() != null
				&& invariantField.getText() != "") {
			return true;
		} else {
			return false;
		}
	}

	public mxCell getCurrentCell() {
		return currentCell;
	}

	public void setCurrentCell(mxCell currentCell) {
		this.currentCell = currentCell;
	}
}
