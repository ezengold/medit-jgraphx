package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.*;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.util.*;
import com.mxgraph.util.*;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.view.mxGraph;

import ui.MeGraph;
import ui.MeGraphComponent;
import ui.MeToolBar;
import ui.MeMenuBar;

public class App extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * 
	 */
	protected JPanel navComponent;

	/**
	 * 
	 */
	protected mxGraphComponent graphComponent;

	/**
	 * 
	 */
	protected mxGraphOutline graphOutline;

	/**
	 * 
	 */
	protected mxUndoManager undoManager;

	/**
	 * 
	 */
	protected String appTitle;

	/**
	 * 
	 */
	protected JLabel statusBar;

	/**
	 * 
	 */
	protected File currentFile;

	/**
	 * Flag indicating whether the current graph has been modified
	 */
	protected boolean modified = false;

	/**
	 * 
	 */
	protected mxKeyboardHandler keyboardHandler;

	/**
	 * 
	 */
	protected mxIEventListener undoHandler = new mxIEventListener() {
		public void invoke(Object source, mxEventObject evt) {
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

	public App() {
		this.appTitle = "Nouveau graph";
		this.graphComponent = new MeGraphComponent(new MeGraph());

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
		
		// Install the tool bar
		installToolBar();
	}

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

	public JFrame createFrame(MeMenuBar menuBar) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(this);
		frame.setLocationRelativeTo(null);
		frame.setSize(1000, 700);
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
		this.modified = modified;
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
					status("Repaint all" + buffer);
				} else {
					status("Repaint: x=" + (int) (dirty.getX()) + " y=" + (int) (dirty.getY()) + " w="
							+ (int) (dirty.getWidth()) + " h=" + (int) (dirty.getHeight()) + buffer);
				}
			}
		});
	}

	public void exit() {
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null) {
			frame.dispose();
		}
	}

}
