package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;

import models.Transition;

public class ConfigTransitionEdge extends JDialog {

	private static final long serialVersionUID = 7296229285381074653L;

	protected mxIGraphModel graphModel;

	private mxCell currentCell;

	protected JTextArea guardField = new JTextArea();

	protected JTextArea updateField = new JTextArea();

	public ConfigTransitionEdge(final mxCell cell, final mxIGraphModel graphModel) {
		super((Frame) null, "Transition", true);
		setModal(true);
		setPreferredSize(new Dimension(400, 400));

		setLocation(500, 200);

		Transition transition = (Transition) cell.getValue();

		this.graphModel = graphModel;
		this.currentCell = cell;

		JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));

		panel.add(new JLabel("Garde :"));
		guardField.setBorder(new EmptyBorder(10, 10, 10, 10));
		guardField.setText(transition.getGuardInstructions());
		panel.add(guardField);

		panel.add(new JLabel("Mise à jour :"));
		guardField.setBorder(new EmptyBorder(10, 10, 10, 10));
		updateField.setText(transition.getUpdateInstructions());
		panel.add(updateField);

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
		setResizable(true);
	}

	public boolean applyChanges() {
		if (formIsValid()) {
			Transition trans = (Transition) currentCell.getValue();

			trans.setGuardInstructions(guardField.getText());
			trans.setUpdateInstructions(updateField.getText());

			graphModel.setValue(currentCell, trans);

			return true;
		} else {
			return false;
		}
	}

	public boolean formIsValid() {
		if (guardField.getText() != null && guardField.getText() != "" && updateField.getText() != null
				&& updateField.getText() != "") {
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
