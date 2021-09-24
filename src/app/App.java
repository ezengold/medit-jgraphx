package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.List;

import javax.swing.*;

import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.util.*;
import com.mxgraph.util.*;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.view.mxGraph;

import models.State;
import models.Transition;
import ui.ConfigStateDialog;
import ui.ConfigTransitionDialog;
import ui.MeGraphComponent;
import ui.MeToolBar;
import utils.EditorKeyboardHandler;
import ui.MeMenuBar;
import ui.MePalette;

public class App extends JPanel {

	private static final long serialVersionUID = -8150527452734294724L;

	/*
	 * Panel showing the working tree
	 */
	protected JPanel navComponent;

	/**
	 * Contains the architecture of the graph
	 */
	protected DefaultDirectedGraph<State, Transition> graphData;

	/*
	 * Adapter of the graph
	 */
	protected JGraphXAdapter<State, Transition> jgxAdapter;

	/*
	 * UI that displays the palette
	 */
	protected MePalette palette;

	/**
	 * 
	 */
	protected mxGraphComponent graphComponent;

	/**
	 * UI that display an outline of the graph, help to resize the paper
	 */
	protected mxGraphOutline graphOutline;

	/**
	 * Tool used to handle undo actions (store latest actions)
	 */
	protected mxUndoManager undoManager;

	/**
	 * Title of the Application
	 */
	protected String appTitle;

	/**
	 * Label displayed at the bottom to show the status of the Application
	 */
	protected JLabel statusBar;

	/**
	 * Current file where the changes are saved to
	 */
	protected File currentFile;

	/**
	 * Flag indicating whether the current graph has been modified
	 */
	protected boolean modified = false;

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
//			List<mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
//			for (mxUndoableChange ch : changes) {
//				System.out.println(ch.toString());
//			}
			undoManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));
		}
	};

	/**
	 * 
	 */
	protected mxIEventListener changeTracker = new mxIEventListener() {
		public void invoke(Object source, mxEventObject evt) {
			setModified(true);
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
					if (sourceState != null && targetState != null) {
						graphData.addEdge(sourceState, targetState, t);
					}
				} else if (el.isVertex()) {
					State s = new State();
					el.setValue(s);
					graphData.addVertex(s);
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
			Object[] cells = (Object[]) evt.getProperty("cells");

			for (Object c : cells) {
				mxCell el = (mxCell) c;
				if (el.isEdge()) {
					Transition t = (Transition) el.getValue();
					graphData.removeEdge(t);
				} else if (el.isVertex()) {
					State s = (State) el.getValue();
					graphData.removeVertex(s);
				}
			}

			// Check if all cells have been removed then add a new vertex
			final mxGraph graph = graphComponent.getGraph();
			Object[] remains = graph.getChildCells(graph.getDefaultParent());

			if (remains.length == 0) {
				State newState = new State();
				graphData.addVertex(newState);
				graph.insertVertex(graph.getDefaultParent(), newState.getName(), newState, 20, 20, 20, 20);
			}
		}
	};

	protected mxIEventListener testHandler = new mxIEventListener() {

		@Override
		public void invoke(Object source, mxEventObject evt) {
			System.out.println(evt.getProperties());
		}
	};

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
				mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";

				App app = new App();

				app.createFrame(new MeMenuBar(app)).setVisible(true);
			}
		});
	}

	public App() {
		this.appTitle = "Medit";

		// Initiate graph with initial state
		this.graphData = new DefaultDirectedGraph<State, Transition>(Transition.class);

		State s0 = new State();
		graphData.addVertex(s0);

		this.jgxAdapter = new JGraphXAdapter<State, Transition>(graphData);

		this.graphComponent = new MeGraphComponent(jgxAdapter);

		final mxGraph graph = graphComponent.getGraph();

		undoManager = createUndoManager();

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
								ConfigTransitionDialog confDialog = new ConfigTransitionDialog(el, graph.getModel());
								confDialog.setVisible(true);
							}
						} else if (el.isVertex()) {
							State state = (State) el.getValue();

							if (state != null) {
								ConfigStateDialog confDialog = new ConfigStateDialog(el, graph.getModel());
								confDialog.setVisible(true);
							}
						}
					}
				}
			}
		});

		graphComponent.getGraphControl().addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				//
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				//
			}
		});

		// Keeps the selection in sync with the command history
		mxIEventListener undoHandler = new mxIEventListener() {
			public void invoke(Object source, mxEventObject evt) {
				List<mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
				graph.setSelectionCells(graph.getSelectionCellsForChanges(changes));
			}
		};

		this.undoManager.addListener(mxEvent.UNDO, undoHandler);
		this.undoManager.addListener(mxEvent.REDO, undoHandler);

		// Creates the graph outline component
		this.graphOutline = new mxGraphOutline(graphComponent);

		// Create instance of palette
		this.palette = new MePalette();

		this.navComponent = new JPanel();
		this.navComponent.setBackground(Color.LIGHT_GRAY);

		JSplitPane leftInner = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.navComponent, this.graphOutline);
		leftInner.setDividerLocation(350);
		leftInner.setResizeWeight(1);
		leftInner.setDividerSize(3);
		leftInner.setBorder(null);

		JPanel leftWrapper = new JPanel(new BorderLayout());
//		leftWrapper.add(this.palette, BorderLayout.NORTH);
		leftWrapper.add(leftInner, BorderLayout.CENTER);
		leftWrapper.setBorder(null);

		JSplitPane mainWrapper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftWrapper, this.graphComponent);
		mainWrapper.setOneTouchExpandable(true);
		mainWrapper.setDividerLocation(200);
		mainWrapper.setDividerSize(3);
		mainWrapper.setBorder(null);

		// Creates the status bar
		statusBar = createStatusBar();

		this.setLayout(new BorderLayout());
		this.add(mainWrapper, BorderLayout.CENTER);
		this.add(statusBar, BorderLayout.SOUTH);

		// Handle when graph repaint Event
		installRepaintListener();

		// Install keyboard and rubber band handlers
		installHandlers();
		installListeners();

		updateTitle();
	}

	public JFrame createFrame(MeMenuBar menuBar) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(this);
		frame.setLocationRelativeTo(null);
		frame.setSize(800, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(menuBar);

		this.updateTitle();

		return frame;
	}

	protected mxUndoManager createUndoManager() {
		return new mxUndoManager();
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		boolean oldValue = this.modified;
		this.modified = modified;

		firePropertyChange("modified", oldValue, modified);

		if (oldValue != modified) {
			updateTitle();
		}
	}

	public File getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(File file) {
		File oldValue = currentFile;
		currentFile = file;

		firePropertyChange("currentFile", oldValue, file);

		if (oldValue != file) {
			//
		}
	}

	public void updateTitle() {
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null) {
			String title = (currentFile != null) ? currentFile.getAbsolutePath() : "Nouveau Model";

			if (modified) {
				title += "*";
			}

			frame.setTitle(title + " - " + appTitle);
		}
	}

	public mxGraphComponent getGraphComponent() {
		return graphComponent;
	}

	public mxGraphOutline getGraphOutline() {
		return graphOutline;
	}

	public JPanel getNavComponent() {
		return navComponent;
	}

	public void setNavComponent(JPanel navComponent) {
		this.navComponent = navComponent;
	}

	public mxUndoManager getUndoManager() {
		return undoManager;
	}

	protected void installToolBar() {
		this.add(new MeToolBar(this, JToolBar.HORIZONTAL), BorderLayout.NORTH);
	}

	protected JLabel createStatusBar() {
		JLabel statusBar = new JLabel("Prêt");
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		return statusBar;
	}

	public void status(String msg) {
		statusBar.setText(msg);
	}

	protected void installRepaintListener() {
		graphComponent.getGraph().addListener(mxEvent.REPAINT, new mxIEventListener() {
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
					App.this.mouseWheelMoved(e);
				}
			}
		};
		graphOutline.addMouseWheelListener(wheelTracker);
		graphComponent.addMouseWheelListener(wheelTracker);

		graphOutline.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
//					showOutlinePopupMenu(e);
				}
			}
		});

		graphComponent.getGraphControl().addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				mouseLocationChanged(e);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				mouseDragged(e);
			}
		});
	}

	protected void mouseLocationChanged(MouseEvent e) {
		status(e.getX() + ", " + e.getY());
	}

	public void exit() {
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null) {
			frame.dispose();
		}
	}

	/**
	 * 
	 * @param name
	 * @param action
	 * @return a new Action bound to the specified string name
	 */
	public Action bind(String name, final Action action) {
		return bind(name, action, null);
	}

	/**
	 * 
	 * @param name
	 * @param action
	 * @return a new Action bound to the specified string name and icon
	 */
	@SuppressWarnings("serial")
	public Action bind(String name, final Action action, String iconUrl) {
		AbstractAction newAction = new AbstractAction(name,
				(iconUrl != null) ? new ImageIcon(App.class.getResource(iconUrl)) : null) {
			public void actionPerformed(ActionEvent e) {
				action.actionPerformed(new ActionEvent(getGraphComponent(), e.getID(), e.getActionCommand()));
			}
		};

		newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));

		return newAction;
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
}
