package app;

import java.awt.*;

import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import models.Automata;
import ui.Button;
import ui.TraceCellRenderer;
import ui.TracesTableModel;
import ui.TransitionsTableModel;
import ui.TransitonCellRenderer;

public class Simulator extends JPanel {

	private static final long serialVersionUID = 8364570541969774335L;

	private static String[] ACTIVES_TRANSITIONS_TABLE_COLUMNS = { "Transitions" };

	private static String[] TRACES_TABLE_COLUMNS = { "Liste des traces" };

	private App app;

	private Automata automata;

	private JPanel activeTransitionsPanel;

	private Object[][] activeTransitions = { { "Transition 0" }, { "Transition 1" }, { "Transition 2" } };

	private JTable activeTransitionsTable;

	private TransitionsTableModel activeTransitionsTableModel;

	private Button nextTransitionButton;

	private Button previousTransitionButton;

	private JPanel tracesPanel;

	private Object[][] traces = { { "" } };

	private JTable tracesTable;

	private TracesTableModel tracesTableModel;

	private JPanel variablesPanel;

	private JPanel apercuPanel;

	public Simulator(App app) {
		this.app = app;
		this.automata = app.getAutomata();

		// ACTIVES TRANSITIONS
		this.activeTransitionsPanel = new JPanel(new BorderLayout());
		activeTransitionsPanel.setBorder(BorderFactory.createTitledBorder(" Transitions actives "));

		this.activeTransitionsTableModel = new TransitionsTableModel(activeTransitions, ACTIVES_TRANSITIONS_TABLE_COLUMNS);
		this.activeTransitionsTable = new JTable(activeTransitionsTableModel);
		activeTransitionsTable.setRowHeight(35);
		activeTransitionsTable.setTableHeader(null);
		setFileChooserFont(activeTransitionsTable.getComponents());
		activeTransitionsTable.getColumn(ACTIVES_TRANSITIONS_TABLE_COLUMNS[0]).setCellRenderer(new TransitonCellRenderer());

		JScrollPane activeTransitionsTableScrollPane = new JScrollPane(activeTransitionsTable);
		activeTransitionsTableScrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		activeTransitionsPanel.add(activeTransitionsTableScrollPane, BorderLayout.CENTER);

		this.nextTransitionButton = new Button("Suivant");
		nextTransitionButton.setBackground(Color.decode("#9099ae"));

		this.previousTransitionButton = new Button("Previous");
		previousTransitionButton.setBackground(Color.decode("#9099ae"));

		JPanel activesTransitionsButtonsContainer = new JPanel();
		activesTransitionsButtonsContainer.setBorder(new EmptyBorder(0, 5, 5, 5));
		activesTransitionsButtonsContainer.add(previousTransitionButton);
		activesTransitionsButtonsContainer.add(nextTransitionButton);

		activeTransitionsPanel.add(activesTransitionsButtonsContainer, BorderLayout.SOUTH);

		// SIMULATION TRACES
		this.tracesPanel = new JPanel(new BorderLayout());
		this.tracesPanel.setBorder(BorderFactory.createTitledBorder(" Traces de simulation "));

		this.tracesTableModel = new TracesTableModel(traces, TRACES_TABLE_COLUMNS);
		this.tracesTable = new JTable(tracesTableModel);
		tracesTable.setRowHeight(35);
		tracesTable.setTableHeader(null);
		setFileChooserFont(tracesTable.getComponents());
		tracesTable.getColumn(TRACES_TABLE_COLUMNS[0]).setCellRenderer(new TraceCellRenderer());

		JScrollPane tracesTableScrollPane = new JScrollPane(tracesTable);
		tracesTableScrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		tracesPanel.add(tracesTableScrollPane, BorderLayout.CENTER);

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

	public TransitionsTableModel getActiveTransitionsTableModel() {
		return activeTransitionsTableModel;
	}

	public void setActiveTransitionsTableModel(TransitionsTableModel activeTransitionsTableModel) {
		this.activeTransitionsTableModel = activeTransitionsTableModel;
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

	public Object[][] getTraces() {
		return traces;
	}

	public void setTraces(Object[][] traces) {
		this.traces = traces;
	}

	public JTable getTracesTable() {
		return tracesTable;
	}

	public void setTracesTable(JTable tracesTable) {
		this.tracesTable = tracesTable;
	}

	public TracesTableModel getTracesTableModel() {
		return tracesTableModel;
	}

	public void setTracesTableModel(TracesTableModel tracesTableModel) {
		this.tracesTableModel = tracesTableModel;
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

	public void setFileChooserFont(Component[] comps) {
		for (Component comp : comps) {
			if (comp instanceof Container) {
				setFileChooserFont(((Container) comp).getComponents());
			}

			try {
				comp.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
			} catch (Exception e) {
				//
			}
		}
	}
}
