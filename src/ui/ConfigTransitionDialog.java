package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;

import models.Transition;

public class ConfigTransitionDialog extends JDialog {

	private static final long serialVersionUID = 7296229285381074653L;

	protected mxIGraphModel graphModel;

	private mxCell currentCell;

	protected JTextArea guardField = new JTextArea(10, 10);

	protected JTextArea updateField = new JTextArea(10, 10);

	public ConfigTransitionDialog(final mxCell cell, final mxIGraphModel graphModel) {
		super((Frame) null, "Transition", true);
		setModal(true);
		setPreferredSize(new Dimension(400, 300));

		setLocation(500, 200);

		Transition transition = (Transition) cell.getValue();

		this.graphModel = graphModel;
		this.currentCell = cell;

		JPanel panel = new JPanel(new GridLayout(4, 1));

		JLabel guardLabel = new JLabel("Garde :");
		guardLabel.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
		guardLabel.setBorder(new EmptyBorder(0, 5, 5, 5));
		panel.add(guardLabel);
		guardField.setBorder(new EmptyBorder(5, 5, 5, 5));
		guardField.setLineWrap(true);
		guardField.setWrapStyleWord(true);
		guardField.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
		guardField.setText(transition.getGuard());
		JScrollPane scrollGuardField = new JScrollPane(guardField);
		panel.add(scrollGuardField);

		JLabel updateLabel = new JLabel("Mise à jour :");
		updateLabel.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
		updateLabel.setBorder(new EmptyBorder(15, 5, 5, 5));
		panel.add(updateLabel);
		updateField.setBorder(new EmptyBorder(5, 5, 5, 5));
		updateField.setLineWrap(true);
		updateField.setWrapStyleWord(true);
		updateField.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
		updateField.setText(transition.getUpdate());
		JScrollPane scrollUpdateField = new JScrollPane(updateField);
		panel.add(scrollUpdateField);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBorder(new EmptyBorder(20, 5, 5, 5));

		Button submitButton = new Button("Valider");
		submitButton.setBackground(Color.decode("#9099ae"));
		submitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (applyChanges()) {
					setVisible(false);
				}
			}
		});

		Button cancelButton = new Button("Annuler");
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

			trans.setGuard(guardField.getText());
			trans.setUpdate(updateField.getText());

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
