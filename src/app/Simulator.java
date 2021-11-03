package app;

import java.awt.*;

import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import models.Automata;
import ui.Button;
import ui.TransitionsTable;

public class Simulator extends JPanel {

	private static final long serialVersionUID = 8364570541969774335L;

	private static String[] ACTIVES_TRANSITIONS_TABLE_COLUMNS = { "Transitions" };

	private App app;

	private Automata automata;

	private JPanel activeTransitionsPanel;

	private Object[][] activeTransitions = { { "Transition 0" }, { "Transition 1" }, { "Transition 2" } };

	private TransitionsTable activeTransitionsTable;

	private Button nextTransitionButton;

	private Button previousTransitionButton;

	private JPanel tracesPanel;

	private JPanel variablesPanel;

	private JPanel apercuPanel;

	public Simulator(App app) {
		this.app = app;
		this.automata = app.getAutomata();

		this.activeTransitionsPanel = new JPanel(new BorderLayout());
		activeTransitionsPanel.setBorder(BorderFactory.createTitledBorder(" Transitions actives "));

		this.activeTransitionsTable = new TransitionsTable(this.activeTransitions, ACTIVES_TRANSITIONS_TABLE_COLUMNS);
		JScrollPane tableScrollPane = new JScrollPane(activeTransitionsTable);
		tableScrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		activeTransitionsPanel.add(tableScrollPane, BorderLayout.CENTER);

		this.nextTransitionButton = new Button("Suivant");
		nextTransitionButton.setBackground(Color.decode("#9099ae"));

		this.previousTransitionButton = new Button("Previous");
		previousTransitionButton.setBackground(Color.decode("#9099ae"));

		JPanel activesTransitionsButtonsContainer = new JPanel();
		activesTransitionsButtonsContainer.setBorder(new EmptyBorder(0, 5, 5, 5));
		activesTransitionsButtonsContainer.add(previousTransitionButton);
		activesTransitionsButtonsContainer.add(nextTransitionButton);

		activeTransitionsPanel.add(activesTransitionsButtonsContainer, BorderLayout.SOUTH);

		this.tracesPanel = new JPanel();
		this.tracesPanel.setBorder(BorderFactory.createTitledBorder(" Traces de simulation "));

		JSplitPane leftInnerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, activeTransitionsPanel, tracesPanel);
		leftInnerSplit.setDividerLocation(350);
		leftInnerSplit.setResizeWeight(1);
		leftInnerSplit.setDividerSize(3);
		leftInnerSplit.setBorder(null);

		this.variablesPanel = new JPanel();
		this.variablesPanel.setBorder(BorderFactory.createTitledBorder(" Variables "));

		JSplitPane leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftInnerSplit, variablesPanel);
		leftSplit.setDividerLocation(350);
		leftSplit.setResizeWeight(1);
		leftSplit.setDividerSize(3);
		leftSplit.setBorder(null);

		this.apercuPanel = new JPanel();

		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplit, apercuPanel);
		mainSplit.setDividerLocation(600);
		mainSplit.setResizeWeight(1);
		mainSplit.setDividerSize(3);
		mainSplit.setBorder(null);

		this.setLayout(new BorderLayout());
		this.add(mainSplit, BorderLayout.CENTER);
	}

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	public Automata getAutomata() {
		return automata;
	}

	public void setAutomata(Automata automata) {
		this.automata = automata;
	}

	public JPanel getActiveTransitionsPanel() {
		return activeTransitionsPanel;
	}

	public void setActiveTransitionsPanel(JPanel activeTransitionsPanel) {
		this.activeTransitionsPanel = activeTransitionsPanel;
	}

	public TransitionsTable getActiveTransitionsTable() {
		return activeTransitionsTable;
	}

	public void setActiveTransitionsTable(TransitionsTable activeTransitionsTable) {
		this.activeTransitionsTable = activeTransitionsTable;
	}

	public Button getNextTransitionButton() {
		return nextTransitionButton;
	}

	public void setNextTransitionButton(Button nextTransitionButton) {
		this.nextTransitionButton = nextTransitionButton;
	}

	public Button getPreviousTransitionButton() {
		return previousTransitionButton;
	}

	public void setPreviousTransitionButton(Button previousTransitionButton) {
		this.previousTransitionButton = previousTransitionButton;
	}

	public JPanel getTracesPanel() {
		return tracesPanel;
	}

	public void setTracesPanel(JPanel tracesPanel) {
		this.tracesPanel = tracesPanel;
	}

	public JPanel getVariablesPanel() {
		return variablesPanel;
	}

	public void setVariablesPanel(JPanel variablesPanel) {
		this.variablesPanel = variablesPanel;
	}

	public JPanel getApercuPanel() {
		return apercuPanel;
	}

	public void setApercuPanel(JPanel apercuPanel) {
		this.apercuPanel = apercuPanel;
	}
}
