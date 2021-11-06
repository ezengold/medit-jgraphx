package app;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mxgraph.view.mxGraph;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import models.Automata;
import models.State;
import models.Trace;
import models.Transition;
import ui.Button;
import ui.SimulatorGraphComponent;
import ui.TraceCellRenderer;
import ui.TracesTableModel;
import ui.TransitionsTableModel;
import ui.TransitonCellRenderer;
import ui.VariablesTree;
import utils.Observer;

public class Simulator extends JPanel {

	private static final long serialVersionUID = 8364570541969774335L;

	private static String ACTIVES_TRANSITIONS_TABLE_COLUMN_NAME = "Transitions";

	private static String TRACES_TABLE_COLUMN_NAME = "Liste des traces";

	private App app;

	private Automata automata;

	// handle active transaction

	private State currentState;

	private Transition currentTransition;

	private JPanel activeTransitionsPanel;

	private JTable activeTransitionsTable;

	private TransitionsTableModel activeTransitionsTableModel;

	private Button nextTransitionButton;

	private Button previousTransitionButton;

	// handle simulation traces

	private JPanel tracesPanel;

	private ArrayList<Trace> traces = new ArrayList<Trace>();

	private JTable tracesTable;

	private TracesTableModel tracesTableModel;

	// handle variable tree

	private VariablesTree variablesTree;

	// handle visualization

	private SimulatorGraphComponent graphComponent;

	public Simulator(App app) {
		this.app = app;
		this.automata = app.getAutomata();

		this.setCurrentState(automata.getInitialState());

		// ACTIVES TRANSITIONS
		this.activeTransitionsPanel = new JPanel(new BorderLayout());
		activeTransitionsPanel.setBorder(new CompoundBorder(BorderFactory.createTitledBorder(" Transitions actives "),
				new EmptyBorder(0, 5, 5, 5)));

		this.activeTransitionsTableModel = new TransitionsTableModel(ACTIVES_TRANSITIONS_TABLE_COLUMN_NAME);
		this.activeTransitionsTable = new JTable(activeTransitionsTableModel);
		activeTransitionsTable.setRowHeight(35);
		activeTransitionsTable.setTableHeader(null);
		activeTransitionsTable.getColumnModel().getSelectionModel()
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setFileChooserFont(activeTransitionsTable.getComponents());
		activeTransitionsTable.getColumn(ACTIVES_TRANSITIONS_TABLE_COLUMN_NAME)
				.setCellRenderer(new TransitonCellRenderer(automata));

		JScrollPane activeTransitionsTableScrollPane = new JScrollPane(activeTransitionsTable);
		activeTransitionsTableScrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		activeTransitionsPanel.add(activeTransitionsTableScrollPane, BorderLayout.CENTER);

		this.nextTransitionButton = new Button("Suivant");
		nextTransitionButton.setBackground(Color.decode("#9099ae"));
		nextTransitionButton.setEnabled(false);

		this.previousTransitionButton = new Button("Previous");
		previousTransitionButton.setEnabled(false);

		installActiveTransitionsHandlers();

		JPanel activesTransitionsButtonsContainer = new JPanel();
		activesTransitionsButtonsContainer.setBorder(new EmptyBorder(5, 10, 0, 10));
		activesTransitionsButtonsContainer.add(previousTransitionButton);
		activesTransitionsButtonsContainer.add(nextTransitionButton);

		activeTransitionsPanel.add(activesTransitionsButtonsContainer, BorderLayout.SOUTH);

		// SIMULATION TRACES
		this.tracesPanel = new JPanel(new BorderLayout());
		this.tracesPanel.setBorder(new CompoundBorder(BorderFactory.createTitledBorder(" Traces de simulation "),
				new EmptyBorder(0, 5, 5, 5)));

		this.tracesTableModel = new TracesTableModel(TRACES_TABLE_COLUMN_NAME);
		this.tracesTable = new JTable(tracesTableModel);
		tracesTable.setRowHeight(35);
		tracesTable.setTableHeader(null);
		tracesTable.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setFileChooserFont(tracesTable.getComponents());
		tracesTable.getColumn(TRACES_TABLE_COLUMN_NAME).setCellRenderer(new TraceCellRenderer(traces));

		JScrollPane tracesTableScrollPane = new JScrollPane(tracesTable);
		tracesTableScrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		tracesPanel.add(tracesTableScrollPane, BorderLayout.CENTER);

		JSplitPane leftInnerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, activeTransitionsPanel, tracesPanel);
		leftInnerSplit.setDividerLocation(350);
		leftInnerSplit.setResizeWeight(1);
		leftInnerSplit.setDividerSize(3);
		leftInnerSplit.setBorder(null);

		// VARIABLES
		this.variablesTree = new VariablesTree(automata);

		JSplitPane leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftInnerSplit, variablesTree);
		leftSplit.setDividerLocation(350);
		leftSplit.setResizeWeight(1);
		leftSplit.setDividerSize(3);
		leftSplit.setBorder(null);

		// VISUALIZATION
		this.graphComponent = new SimulatorGraphComponent(app.graphComponent.getGraph());

		JPanel graphComponentPanel = new JPanel(new BorderLayout());
		graphComponentPanel.setBorder(new CompoundBorder(BorderFactory.createTitledBorder(" Visualisation "), new EmptyBorder(0, 5, 5, 5)));

		JScrollPane graphComponentScrollPane = new JScrollPane(graphComponent);
		graphComponentScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

		graphComponentPanel.add(graphComponentScrollPane, BorderLayout.CENTER);

		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplit, graphComponentPanel);
		mainSplit.setDividerLocation(600);
		mainSplit.setResizeWeight(1);
		mainSplit.setDividerSize(3);
		mainSplit.setBorder(null);

		setLayout(new BorderLayout());
		add(mainSplit, BorderLayout.CENTER);

		// listen to changes on automata
		automata.addObserver(new Observer() {
			@Override
			public void update(Object data) {
				variablesTree.recreateTree();
			}
		});
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

	/**
	 * ACTIVES TRANSITIONS
	 */
	public void setCurrentTransition(Transition currentTransition) {
		this.currentTransition = currentTransition;
	}

	public Transition getCurrentTransition() {
		return currentTransition;
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;

		if (currentState != null) {
			// clear transitions model and recreate
			activeTransitionsTableModel.removeAllTransitions();

			for (Transition tr : automata.findOutgoingValidTransitions(currentState.getStateId())) {
				activeTransitionsTableModel.addTransition(tr.getTransitionId());
			}

			activeTransitionsTable.revalidate();
			activeTransitionsTable.repaint();
		}
	}

	public State getCurrentState() {
		return currentState;
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

	public void installActiveTransitionsHandlers() {
		nextTransitionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleNextClicked();
			}
		});

		previousTransitionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handlePreviousClicked();
			}
		});

		// handle selected transition in the table to enable or disable buttons
		activeTransitionsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedRow = activeTransitionsTable.getSelectedRow();
				if (selectedRow != -1) {
					Transition clickedTransition = automata
							.findTransition(activeTransitionsTable.getValueAt(selectedRow, 0).toString());
					if (clickedTransition != null) {
						setCurrentTransition(clickedTransition);
						nextTransitionButton.setEnabled(true);
					} else {
						nextTransitionButton.setEnabled(false);
					}
				}
			}
		});
	}

	public void handleNextClicked() {
		Transition oldCurrentTransition = currentTransition;

		saveTrace();

		setCurrentState(automata.findState(oldCurrentTransition.getTargetStateId()));
		nextTransitionButton.setEnabled(false);
	}

	public void handlePreviousClicked() {
		//
	}

	/**
	 * TRACES HANDLING
	 */
	public JPanel getTracesPanel() {
		return tracesPanel;
	}

	public void setTracesPanel(JPanel tracesPanel) {
		this.tracesPanel = tracesPanel;
	}

	public void saveTrace() {
		if (currentTransition != null && currentState != null) {
			State targetState = automata.findState(currentTransition.getTargetStateId());

			Trace newTrace = new Trace(currentTransition, currentState, targetState, automata.getEngine());

			traces.add(newTrace);

			// insert in the trace table model
			tracesTableModel.addTrace(newTrace.getTraceId());
			tracesTable.revalidate();
		}
	}

	public ArrayList<Trace> getTraces() {
		return traces;
	}

	public void setTraces(ArrayList<Trace> traces) {
		this.traces = traces;

		// reset these traces to the modal
		tracesTableModel.removeAllTraces();

		for (Trace trace : traces) {
			tracesTableModel.addTrace(trace.getTraceId());
		}

		tracesTable.revalidate();
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

	/**
	 * VARIABLE TREE
	 */
	public VariablesTree getVariablesTree() {
		return variablesTree;
	}

	public void setVariablesTree(VariablesTree variablesTree) {
		this.variablesTree = variablesTree;
	}

	public SimulatorGraphComponent getGraphComponent() {
		return graphComponent;
	}

	public void setGraphComponent(SimulatorGraphComponent graphComponent) {
		this.graphComponent = graphComponent;
	}

	public void recreateSimulatorGraph(final mxGraph newGraph) {
		this.graphComponent.setGraph(newGraph);
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
