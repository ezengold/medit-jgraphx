package app;

import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.view.mxGraph;

import models.Automata;
import models.Notification;
import models.State;
import models.Transition;
import models.Notification.Name;
import ui.ConfigStateDialog;
import ui.ConfigTransitionDialog;
import ui.GraphComponent;
import utils.EditorKeyboardHandler;
import utils.Observable;
import utils.Observer;

public class Editor extends JPanel implements Observable {
	/**
	 * List of observers to the object
	 */
	private ArrayList<Observer> observersList = new ArrayList<Observer>();

	/*
	 * Hold the current automata model
	 */
	protected Automata automata;

	/*
	 * Panel wrapping the declarations textarea
	 */
	protected JPanel decsPanel;

	/*
	 * Input holding the global declarations
	 */
	protected JTextArea decsArea = new JTextArea();

	/*
	 * Global declarations
	 */
	protected String declarations = "";

	/*
	 * Component that wrap the mxGraph
	 */
	protected mxGraphComponent graphComponent;

	/*
	 * UI that display an outline of the graph, help to resize the paper
	 */
	protected mxGraphOutline graphOutline;

	/**
	 * Tool used to handle undo actions (store latest actions)
	 */
	protected mxUndoManager undoManager;

	/**
	 * Used to handle rectangular region selection in the graph
	 */
	protected mxRubberband rubberband;

	/**
	 * Used to handle keyboard actions, Accelerators by example
	 */
	protected mxKeyboardHandler keyboardHandler;

	/**
	 * 
	 */
	protected mxIEventListener undoHandler = new mxIEventListener() {
		public void invoke(Object source, mxEventObject evt) {
			undoManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));

			updateObservers(new Notification(Name.GRAPH_MODIFIED, true));
		}
	};

	/*
	 * New cell added to the view listener
	 */
	protected mxIEventListener cellsAddedHandler = new mxIEventListener() {

		@Override
		public void invoke(Object source, mxEventObject evt) {
			Object[] cells = (Object[]) evt.getProperty("cells");

			for (Object c : cells) {
				mxCell el = (mxCell) c;
				if (el.isEdge()) {
					Transition t = new Transition();
					State sourceState = null;
					State targetState = null;

					if (el.getSource() != null && el.getSource().getValue() != null) {
						sourceState = (State) el.getSource().getValue();
						t.setSourceStateId(sourceState.getStateId());
					}

					if (el.getTarget() != null && el.getTarget().getValue() != null) {
						targetState = (State) el.getTarget().getValue();
						t.setTargetStateId(targetState.getStateId());
					}

					el.setValue(t);
				} else if (el.isVertex()) {
					State s = new State();
					el.setValue(s);
				}
			}
		}
	};

	/*
	 * Cell removed from the view listener
	 */
	protected mxIEventListener cellsRemovedHandler = new mxIEventListener() {

		@Override
		public void invoke(Object source, mxEventObject evt) {
			// Check if all cells have been removed then add a new vertex
			final mxGraph graph = graphComponent.getGraph();
			Object[] remains = graph.getChildCells(graph.getDefaultParent());

			if (remains.length == 0) {
				State newState = new State();
				graph.insertVertex(graph.getDefaultParent(), newState.getName(), newState, 20, 20, 40, 40);
			}
		}
	};

	public Editor(Automata automata) {
		this.automata = automata;

		State s0 = new State();

		final mxGraph graph = new mxGraph();

		this.graphComponent = new GraphComponent(graph);
		this.graphComponent.setBorder(null);

		JPanel graphComponentPanel = new JPanel(new BorderLayout());
		graphComponentPanel.setBorder(
				new CompoundBorder(BorderFactory.createTitledBorder(" Zone de dessin "), new EmptyBorder(0, 5, 5, 5)));
		graphComponentPanel.add(graphComponent, BorderLayout.CENTER);

		graph.insertVertex(graph.getDefaultParent(), s0.getName(), s0, 20, 20, 40, 40);

		undoManager = new mxUndoManager();

		graph.setMultigraph(false);
		graph.setAllowDanglingEdges(false);
		graph.setDisconnectOnMove(false);
		graph.setVertexLabelsMovable(false);

		// Do not change the scale and translation after files have been loaded
		graph.setResetViewOnRootChange(false);

		// Updates the modified flag if the graph model changes
		graph.getModel().addListener(mxEvent.CHANGE, undoHandler);

		// Adds the command history to the model and view
		graph.getModel().addListener(mxEvent.UNDO, undoHandler);
		graph.getView().addListener(mxEvent.UNDO, undoHandler);

		graph.addListener(mxEvent.CELLS_ADDED, cellsAddedHandler);
		graph.addListener(mxEvent.CELLS_REMOVED, cellsRemovedHandler);

		graphComponent.getGraphControl().addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				//
			}

			@Override
			public void mousePressed(MouseEvent e) {
				//
			}

			@Override
			public void mouseExited(MouseEvent e) {
				//
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				//
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					mxCell el = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());

					if (el != null) {
						if (el.isEdge()) {
							Transition trans = (Transition) el.getValue();

							if (trans != null) {
								ConfigTransitionDialog confDialog = new ConfigTransitionDialog(el,
										(mxGraphModel) graphComponent.getGraph().getModel());
								confDialog.setVisible(true);
							}
						} else if (el.isVertex()) {
							State state = (State) el.getValue();

							if (state != null) {
								ConfigStateDialog confDialog = new ConfigStateDialog(el,
										(mxGraphModel) graphComponent.getGraph().getModel());
								confDialog.setVisible(true);
							}
						}
					}
				}
			}
		});

		// Keeps the selection in sync with the command history
		mxIEventListener undoHandler = new mxIEventListener() {
			public void invoke(Object source, mxEventObject evt) {
				List<mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
				graph.setSelectionCells(graph.getSelectionCellsForChanges(changes));
			}
		};

		undoManager.addListener(mxEvent.UNDO, undoHandler);
		undoManager.addListener(mxEvent.REDO, undoHandler);

		// Creates the graph outline component
		this.graphOutline = new mxGraphOutline(graphComponent);
		JPanel graphOutlinePanel = new JPanel(new BorderLayout());
		graphOutlinePanel
				.setBorder(new CompoundBorder(BorderFactory.createTitledBorder(" Zoom "), new EmptyBorder(0, 5, 5, 5)));
		graphOutlinePanel.add(graphOutline, BorderLayout.CENTER);

		this.decsPanel = createDeclarationsComponent();

		JSplitPane topWrapper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.decsPanel, graphOutlinePanel);
		topWrapper.setDividerLocation(400);
		topWrapper.setResizeWeight(1);
		topWrapper.setDividerSize(5);
		topWrapper.setBorder(null);

		JSplitPane spliter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topWrapper, graphComponentPanel);
		spliter.setDividerLocation(150);
		spliter.setResizeWeight(1);
		spliter.setDividerSize(5);
		spliter.setBorder(null);

		setLayout(new BorderLayout());
		this.add(spliter, BorderLayout.CENTER);

		// Handle when graph repaint Event
		installRepaintListener();

		installHandlers();

		installListeners();
	}

	public JPanel createDeclarationsComponent() {
		JPanel nav = new JPanel();
		nav.setLayout(new BorderLayout());
		nav.setBorder(new CompoundBorder(BorderFactory.createTitledBorder(" Déclarations locales "),
				new EmptyBorder(0, 5, 5, 5)));

		this.decsArea = new JTextArea();
		decsArea.setBorder(new EmptyBorder(5, 5, 5, 5));
		decsArea.setLineWrap(true);
		decsArea.setWrapStyleWord(true);
		decsArea.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
		JScrollPane scrollArea = new JScrollPane(decsArea);
		scrollArea.setBorder(new EmptyBorder(5, 5, 5, 5));
		nav.add(scrollArea, BorderLayout.CENTER);

		decsArea.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				//
			}

			@Override
			public void keyReleased(KeyEvent e) {
				declarations = decsArea.getText();

				updateObservers(new Notification(Name.GRAPH_MODIFIED, true));
			}

			@Override
			public void keyPressed(KeyEvent e) {
				//
			}
		});

		return nav;
	}

	public void updateGraph(final mxGraph newGraph) {
		this.undoManager.clear();
		this.getGraphComponent().zoomAndCenter();

		// Do not change the scale and translation after files have been loaded
		newGraph.setResetViewOnRootChange(false);

		// Updates the modified flag if the graph model changes
		newGraph.getModel().addListener(mxEvent.CHANGE, undoHandler);

		// Adds the command history to the model and view
		newGraph.getModel().addListener(mxEvent.UNDO, undoHandler);
		newGraph.getView().addListener(mxEvent.UNDO, undoHandler);

		newGraph.addListener(mxEvent.CELLS_ADDED, cellsAddedHandler);
		newGraph.addListener(mxEvent.CELLS_REMOVED, cellsRemovedHandler);

		this.graphComponent.setGraph(newGraph);
		this.graphOutline.setGraphComponent(graphComponent);

		updateObservers(new Notification(Name.GRAPH_MODIFIED, false));
	}

	public void status(String msg) {
		updateObservers(new Notification(Name.STATUS_MESSAGE, msg));
	}

	protected void installRepaintListener() {
		this.graphComponent.getGraph().addListener(mxEvent.REPAINT, new mxIEventListener() {
			public void invoke(Object source, mxEventObject evt) {
				String buffer = (graphComponent.getTripleBuffer() != null) ? "" : " (unbuffered)";
				mxRectangle dirty = (mxRectangle) evt.getProperty("region");

				if (dirty == null) {
					// No zone selected for repaint
					status("Repaint all" + buffer);
				} else {
					// When an area is selected for repaint
					status("Repaint: x=" + (int) (dirty.getX()) + " y=" + (int) (dirty.getY()) + " w="
							+ (int) (dirty.getWidth()) + " h=" + (int) (dirty.getHeight()) + buffer);
				}
			}
		});
	}

	protected void installHandlers() {
		rubberband = new mxRubberband(graphComponent);
		keyboardHandler = new EditorKeyboardHandler(graphComponent);
	}

	protected void installListeners() {
		// Listener for zooming
		MouseWheelListener wheelTracker = new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getSource() instanceof mxGraphOutline || e.isControlDown()) {
					getInstance().mouseWheelMoved(e);
				}
			}
		};

		graphOutline.addMouseWheelListener(wheelTracker);
		graphComponent.addMouseWheelListener(wheelTracker);

		graphOutline.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					// showOutlinePopupMenu(e);
				}
			}
		});

		graphComponent.getGraphControl().addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				status(e.getX() + ", " + e.getY());
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				mouseDragged(e);
			}
		});
	}

	// LISTENERS
	protected void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0) {
			graphComponent.zoomIn();
		} else {
			graphComponent.zoomOut();
		}
		status("ZOOM : " + (int) (100 * graphComponent.getGraph().getView().getScale()) + "%");
	}

	public Editor getInstance() {
		return this;
	}

	public mxGraphComponent getGraphComponent() {
		return this.graphComponent;
	}

	public void setGraphComponent(mxGraphComponent graphComponent) {
		this.graphComponent = graphComponent;
	}

	@Override
	public void addObserver(Observer observer) {
		this.observersList.add(observer);
	}

	@Override
	public void updateObservers() {
		for (Observer observer : this.observersList) {
			observer.update(this);
		}
	}

	@Override
	public void updateObservers(Notification notification) {
		for (Observer observer : this.observersList) {
			observer.update(notification);
		}
	}

	@Override
	public void removeObserver() {
		this.observersList.clear();
	}
}
