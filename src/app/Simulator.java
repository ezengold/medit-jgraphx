package app;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import models.Automata;

public class Simulator extends JPanel {

	private static final long serialVersionUID = 8364570541969774335L;

	private App app;

	private Automata automata;

	private JPanel activeTransitionsPanel;

	private JPanel tracesPanel;

	private JPanel variablesPanel;

	private JPanel apercuPanel;

	public Simulator(App app) {
		this.app = app;
		this.automata = app.getAutomata();

		this.activeTransitionsPanel = new JPanel();
		this.activeTransitionsPanel.setBorder(BorderFactory.createTitledBorder(" Transitions actives "));

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
		mainSplit.setDividerLocation(550);
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
