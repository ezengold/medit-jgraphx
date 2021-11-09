package app;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import models.Automata;
import models.ClockVariable;
import models.State;
import models.Trace;
import models.Transition;
import ui.Button;
import ui.GraphStyles;
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

	private JTextArea currentTransitionDetails;

	private JPanel activeTransitionsPanel;

	private JTable activeTransitionsTable;

	private TransitionsTableModel activeTransitionsTableModel;

	private Button nextTransitionButton;

	private Button resetTransitionButton;

	private Button simulateButton;

	private Timer simulateTimer;

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

		this.graphComponent = new SimulatorGraphComponent(app.graphComponent.getGraph());

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

		this.nextTransitionButton = new Button("Prendre");
		nextTransitionButton.setBackground(Color.decode(GraphStyles.STROKE_COLOR.toString()));
		nextTransitionButton.setForeground(Color.WHITE);
		nextTransitionButton.setEnabled(false);

		this.resetTransitionButton = new Button("Restaurer");
		resetTransitionButton.setBackground(Color.decode("#9099ae"));

		this.simulateButton = new Button("Simuler");
		simulateButton.setBackground(Color.decode(GraphStyles.INIT_FILL_COLOR.toString()));
		simulateButton.setForeground(Color.WHITE);

		installActiveTransitionsHandlers();

		JPanel activesTransitionsButtonsContainer = new JPanel();
		activesTransitionsButtonsContainer.setBorder(new EmptyBorder(5, 10, 0, 10));
		activesTransitionsButtonsContainer.add(resetTransitionButton);
		activesTransitionsButtonsContainer.add(nextTransitionButton);
		activesTransitionsButtonsContainer.add(simulateButton);

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

		JPanel currentTransitionDetailsPanel = createCurrentTransitionDetailsPanel();

		JSplitPane variablesSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, currentTransitionDetailsPanel,
				variablesTree);
		variablesSplit.setDividerLocation(200);
		variablesSplit.setResizeWeight(1);
		variablesSplit.setDividerSize(3);
		variablesSplit.setBorder(null);

		JSplitPane leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftInnerSplit, variablesSplit);
		leftSplit.setDividerLocation(350);
		leftSplit.setResizeWeight(1);
		leftSplit.setDividerSize(3);
		leftSplit.setBorder(null);

		// VISUALIZATION

		JPanel graphComponentPanel = new JPanel(new BorderLayout());
		graphComponentPanel.setBorder(
				new CompoundBorder(BorderFactory.createTitledBorder(" Visualisation "), new EmptyBorder(0, 5, 5, 5)));

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
		printTransitionDetails(currentTransition);
		setTransitionActiveOnGraph(currentTransition);
	}

	public Transition getCurrentTransition() {
		return currentTransition;
	}

	public JTextArea getCurrentTransitionDetails() {
		return currentTransitionDetails;
	}

	public void setCurrentTransitionDetails(JTextArea currentTransitionDetails) {
		this.currentTransitionDetails = currentTransitionDetails;
	}

	public JPanel createCurrentTransitionDetailsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(new CompoundBorder(BorderFactory.createTitledBorder(" Détails "), new EmptyBorder(0, 5, 5, 5)));

		this.currentTransitionDetails = new JTextArea("");
		currentTransitionDetails.setEditable(false);
		currentTransitionDetails.setLineWrap(true);
		currentTransitionDetails.setWrapStyleWord(true);
		currentTransitionDetails.setAlignmentY(SwingConstants.TOP);
		currentTransitionDetails.setBorder(new EmptyBorder(5, 5, 5, 5));
		currentTransitionDetails.setFont(new Font("Ubuntu Mono", Font.PLAIN, 12));

		JScrollPane innerScrollPane = new JScrollPane(currentTransitionDetails);
		innerScrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		panel.add(innerScrollPane, BorderLayout.CENTER);

		return panel;
	}

	public void printStateDetails(State state) {
		if (state != null) {
			currentTransitionDetails.setText("État : " + state.getName() + "\n\n\nInvariant : "
					+ (state.getInvariant().isEmpty() ? "Aucun invariant" : state.getInvariant()) + "\n");
		} else {
			currentTransitionDetails.setText("");
		}
	}

	public void printTransitionDetails(Transition transition) {
		if (transition != null) {
			currentTransitionDetails
					.setText("Garde : " + (transition.getGuard().isEmpty() ? "Aucune garde" : transition.getGuard())
							+ "\n\n\nMise à jour : "
							+ (transition.getUpdate().isEmpty() ? "Aucun update" : transition.getUpdate()) + "\n");
		} else {
			currentTransitionDetails.setText("");
		}
	}

	public void setTransitionActiveOnGraph(Transition... transitions) {
		if (graphComponent != null && graphComponent.getGraph() != null) {
			mxGraph graph = graphComponent.getGraph();

			for (Object el : graph.getChildCells(graph.getDefaultParent())) {
				if (el instanceof mxCell) {
					mxCell cell = (mxCell) el;

					if (cell.isEdge() && (cell.getValue() != null) && (cell.getValue() instanceof Transition)) {
						Transition transition = (Transition) cell.getValue();

						graph.getModel().beginUpdate();
						try {
							if (isTransitionIn(transition, transitions)) {
								graph.getModel().setStyle(cell,
										"strokeColor=" + GraphStyles.ACTIVE_EDGE_STROKE_COLOR + ";strokeWidth=2");
							} else {
								graph.getModel().setStyle(cell, "");
							}
						} finally {
							graph.getModel().endUpdate();
							graph.refresh();
							graph.repaint();
						}
					}
				}
			}
		}
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;

		if (currentState != null) {
			setStatesActiveOnGraph(currentState);

			// clear transitions model and recreate
			activeTransitionsTableModel.removeAllTransitions();

			ArrayList<Transition> possibleNexts = automata.findOutgoingValidTransitions(currentState.getStateId());

			for (Transition tr : possibleNexts) {
				activeTransitionsTableModel.addTransition(tr.getTransitionId());
			}

			if (possibleNexts.size() > 0) {
				activeTransitionsTable.setRowSelectionInterval(0, 0);
				nextTransitionButton.setEnabled(true);
			} else {
				setCurrentTransition(null);
				nextTransitionButton.setEnabled(false);
			}

			activeTransitionsTable.revalidate();
			activeTransitionsTable.repaint();
		}
	}

	public void setStatesActiveOnGraph(State... states) {
		if (graphComponent != null && graphComponent.getGraph() != null) {
			mxGraph graph = graphComponent.getGraph();

			for (Object el : graph.getChildCells(graph.getDefaultParent())) {
				if (el instanceof mxCell) {
					mxCell cell = (mxCell) el;

					if (cell.isVertex() && (cell.getValue() != null) && (cell.getValue() instanceof State)) {
						State state = (State) cell.getValue();

						graph.getModel().beginUpdate();
						try {
							if (isStateIn(state, states)) {
								graph.getModel().setStyle(cell,
										"fillColor=" + GraphStyles.ACTIVE_FILL_COLOR + ";strokeColor="
												+ GraphStyles.ACTIVE_STROKE_COLOR + ";fontColor="
												+ GraphStyles.ACTIVE_FONT_COLOR);
							} else {
								graph.getModel().setStyle(cell, "");
							}
						} finally {
							graph.getModel().endUpdate();
							graph.refresh();
							graph.repaint();
						}
					}
				}
			}
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

	public Button getResetTransitionButton() {
		return resetTransitionButton;
	}

	public void setResetTransitionButton(Button resetTransitionButton) {
		this.resetTransitionButton = resetTransitionButton;
	}

	public void installActiveTransitionsHandlers() {
		nextTransitionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleNextClicked();
			}
		});

		resetTransitionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleResetClicked();
			}
		});

		simulateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleSimulateClicked();
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

		// make updates on transition
		if (automata.executeUpdates(currentTransition.getUpdate())) {
			variablesTree.recreateTree();

			saveTrace();
			setCurrentState(automata.findState(oldCurrentTransition.getTargetStateId()));
		}
	}

	public void handleResetClicked() {
		stopTimer();

		app.compileProject();

		tracesTableModel.removeAllTraces();
		tracesTable.revalidate();
		tracesTable.repaint();

		setCurrentTransition(null);
		setCurrentState(automata.getInitialState());
	}

	public void handleSimulateClicked() {
		this.simulateTimer = new Timer();
		nextTransitionButton.setEnabled(false);

		// reset all clocks
		// for (String clockName : automata.getClockVariablesList().keySet()) {
		// ClockVariable clockVariable =
		// automata.getClockVariablesList().get(clockName);

		// automata.getEngine().setVariable(clockName, -1);
		// clockVariable.setValue(-1);
		// }

		simulateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (currentState != null && currentTransition == null) {
					// waiting on an invariant

					setStatesActiveOnGraph(currentState);

					printStateDetails(currentState);

					// clear transitions model
					activeTransitionsTableModel.removeAllTransitions();

					if (automata.getEngine().isConditionSatisfied(currentState.getInvariant())) {
						// stay in the state if the invariant is satisfied
						saveTrace();
					} else {
						ArrayList<Transition> possibleNexts = automata
								.findOutgoingValidTransitions(currentState.getStateId());

						for (Transition tr : possibleNexts) {
							activeTransitionsTableModel.addTransition(tr.getTransitionId());
						}

						setCurrentTransition(possibleNexts.get(0));
					}
				} else if (currentState != null && currentTransition != null) {
					// taking the current transition
					Transition oldCurrentTransition = currentTransition;

					// make updates on transition
					if (automata.executeUpdates(currentTransition.getUpdate())) {
						saveTrace();
						setCurrentState(automata.findState(oldCurrentTransition.getTargetStateId()));
					}
				}

				for (String clockName : automata.getClockVariablesList().keySet()) {
					ClockVariable clockVariable = automata.getClockVariablesList().get(clockName);

					automata.getEngine().setVariable(clockName, clockVariable.getValue() + 1);
					clockVariable.setValue(clockVariable.getValue() + 1);
				}

				activeTransitionsTable.revalidate();
				activeTransitionsTable.repaint();
				variablesTree.recreateTree();
			}
		}, 0, 2000);
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
		if (currentState != null && currentTransition != null) {
			State targetState = automata.findState(currentTransition.getTargetStateId());

			Trace newTrace = new Trace(currentTransition, currentState, targetState, automata.getEngine());

			traces.add(newTrace);

			// insert in the trace table model
			tracesTableModel.addTrace(newTrace.getTraceId());
			tracesTable.revalidate();
		} else if (currentState != null && currentTransition == null) {
			Trace newTrace = new Trace(currentState, automata.getEngine());

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

	public Timer getSimulateTimer() {
		return simulateTimer;
	}

	public void setSimulateTimer(Timer simulateTimer) {
		this.simulateTimer = simulateTimer;
	}

	public void stopTimer() {
		if (simulateTimer != null)
			simulateTimer.cancel();
	}

	public void recreateSimulatorGraph(final mxGraph newGraph) {
		Object[] cells = newGraph.getModel().cloneCells(newGraph.getChildCells(newGraph.getDefaultParent(), true, true),
				true);
		mxGraphModel modelCopy = new mxGraphModel();
		mxGraph graphCopy = new mxGraph(modelCopy);
		graphCopy.addCells(cells);
		this.graphComponent.setGraph(graphCopy);
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

	private boolean isStateIn(State state, State[] states) {
		for (State s : states) {
			if (s != null && s.getStateId().equals(state.getStateId()))
				return true;
		}
		return false;
	}

	private boolean isTransitionIn(Transition transition, Transition[] transitions) {
		for (Transition t : transitions) {
			if (t != null && t.getTransitionId().equals(transition.getTransitionId()))
				return true;
		}
		return false;
	}
}
