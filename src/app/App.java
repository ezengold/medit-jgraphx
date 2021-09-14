package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.List;

import javax.swing.*;

import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultListenableGraph;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.util.*;
import com.mxgraph.util.*;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.view.mxGraph;

import models.Transition;
import ui.MeGraph;
import ui.MeGraphComponent;
import ui.MeToolBar;
import ui.MeMenuBar;

public class App extends JPanel {

	private static final long serialVersionUID = -8150527452734294724L;

	/*
	 * Panel showing the working tree
	 */
	protected JPanel navComponent;

	/**
	 * Contains the architecture of the graph
	 */
	protected ListenableGraph<String, Transition> graphArray;

	/*
	 * Adapter that help to draw the graph in the graph component
	 */
	protected JGraphXAdapter<String, Transition> jgxAdapter;

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
			System.out.println(evt.getName());
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
//				mxGraph graph = app.graphComponent.getGraph();
//
//				String v1 = "v1";
//				String v2 = "v2";
//				String v3 = "v3";
//				String v4 = "v4";
//
//				graph.getModel().beginUpdate();
//				try {
//					graph.insertVertex(graph.getDefaultParent(), v1, new State(new JLabel(v1)), 10.0, 10.0, 50.0, 50.0,
//							"ROUNDED");
//				} finally {
//					graph.getModel().endUpdate();
//				}

//				app.graphArray.addVertex(v1);
//				app.graphArray.addVertex(v2);
//				app.graphArray.addVertex(v3);
//				app.graphArray.addVertex(v4);

				app.createFrame(new MeMenuBar(app)).setVisible(true);
			}
		});
	}

	public App() {
		this.appTitle = "Medit";

		this.graphComponent = new MeGraphComponent(new MeGraph());

		// Initiate graph array
		this.graphArray = new DefaultListenableGraph<>(new DefaultDirectedGraph<>(Transition.class));

		final mxGraph graph = graphComponent.getGraph();

		undoManager = createUndoManager();

		// Do not change the scale and translation after files have been loaded
		graph.setResetViewOnRootChange(false);

		// Updates the modified flag if the graph model changes
		graph.getModel().addListener(mxEvent.CHANGE, undoHandler);

		// Adds the command history to the model and view
		graph.getModel().addListener(mxEvent.UNDO, undoHandler);
		graph.getView().addListener(mxEvent.UNDO, undoHandler);

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

		this.navComponent = new JPanel();

		JSplitPane leftWrapper = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.navComponent, this.graphOutline);
		leftWrapper.setDividerLocation(400);
		leftWrapper.setResizeWeight(1);
		leftWrapper.setDividerSize(6);
		leftWrapper.setBorder(null);

		JSplitPane mainWrapper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftWrapper, this.graphComponent);
		mainWrapper.setOneTouchExpandable(true);
		mainWrapper.setDividerLocation(200);
		mainWrapper.setDividerSize(6);
		mainWrapper.setBorder(null);

		// Creates the status bar
		statusBar = createStatusBar();

		this.setLayout(new BorderLayout());
		this.add(mainWrapper, BorderLayout.CENTER);
		this.add(statusBar, BorderLayout.SOUTH);

		// Handle when graph repaint Event
		installRepaintListener();

		// Install the tool bar
		installToolBar();

		// Install keyboard and rubber band handlers
		installHandlers();

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
			String title = (currentFile != null) ? currentFile.getAbsolutePath() : "Nouveau Graph";

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
				System.out.println(evt.getProperties());

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
//		keyboardHandler = new EditorKeyboardHandler(graphComponent);
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
