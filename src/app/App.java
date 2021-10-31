package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mxgraph.io.mxCodecRegistry;
import com.mxgraph.io.mxObjectCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
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
import utils.Compiler;
import utils.EditorFileFilter;
import utils.EditorKeyboardHandler;
import utils.XmlHandler;
import verifier.ast.Program;
import ui.MeMenuBar;

public class App extends JPanel {

	public enum Compilables {
		DECLARATIONS, IDENTIFIERS, CONDITIONS, UPDATES, INVARIANTS
	}

	private static final long serialVersionUID = -8150527452734294724L;

	/*
	 * Panel showing the working tree
	 */
	protected JPanel navComponent;

	protected JTextArea area = new JTextArea();

	/*
	 * Console panel to view errors and status
	 */
	protected Console consolePanel;

	/*
	 * Global declarations
	 */
	protected String globalDeclarations = "";

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
	
	public static File automateFile;

	public static File currentTempFile;
	
	public static Program FinalProgram;

	/**
	 * Compilables elements contains ids, invariants, declarations and conditions...
	 */
	public static HashMap<Compilables, ArrayList<String>> COMPILABLES_ELEMENTS;

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

	/*
	 * Main tab container
	 */
	protected JTabbedPane mainTab;

	protected int wantedTabIndex = 0;

	protected Simulator simulatorPanel;

	protected Verifier verifierPanel;

	protected JFrame mainFrame;

	protected boolean loading = false;

	/**
	 * 
	 */
	protected mxIEventListener undoHandler = new mxIEventListener() {
		public void invoke(Object source, mxEventObject evt) {
			undoManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));
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
				graph.insertVertex(graph.getDefaultParent(), newState.getName(), newState, 20, 20, 20, 20);
			}
		}
	};

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
				mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";

				App app = new App();

				app.createFrame(new MeMenuBar(app)).setVisible(true);
			}
		});
	}

	public App() {
		mxCodecRegistry.addPackage("models");
		mxCodecRegistry.register(new mxObjectCodec(new State()));
		mxCodecRegistry.register(new mxObjectCodec(new Transition()));

		this.appTitle = "Medit";

		State s0 = new State();

		final mxGraph graph = new mxGraph();

		this.graphComponent = new MeGraphComponent(graph);

		graph.insertVertex(graph.getDefaultParent(), s0.getName(), s0, 20, 20, 20, 20);

		undoManager = createUndoManager();

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

		this.undoManager.addListener(mxEvent.UNDO, undoHandler);
		this.undoManager.addListener(mxEvent.REDO, undoHandler);

		// Creates the graph outline component
		this.graphOutline = new mxGraphOutline(graphComponent);

		this.navComponent = createNavComponent();

		JSplitPane leftInner = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.navComponent, this.graphOutline);
		leftInner.setDividerLocation(350);
		leftInner.setResizeWeight(1);
		leftInner.setDividerSize(3);
		leftInner.setBorder(null);

		JPanel leftWrapper = new JPanel(new BorderLayout());
		leftWrapper.add(leftInner, BorderLayout.CENTER);
		leftWrapper.setBorder(null);

		this.consolePanel = createConsolePanel();

		JSplitPane consoleSpliter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphComponent, consolePanel);
		consoleSpliter.setOneTouchExpandable(true);
		consoleSpliter.setDividerLocation(600);
		consoleSpliter.setDividerSize(10);
		consoleSpliter.setBorder(null);

		JSplitPane mainWrapper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftWrapper, consoleSpliter);
		mainWrapper.setOneTouchExpandable(true);
		mainWrapper.setDividerLocation(250);
		mainWrapper.setDividerSize(5);

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

	public App getInstance() {
		return this;
	}

	public JFrame createFrame(MeMenuBar menuBar) {
		this.mainFrame = new JFrame(this.appTitle);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setSize(1200, 700);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setJMenuBar(menuBar);

		this.updateTitle();

		// Create other tabs instances and forward the graph context
		simulatorPanel = new Simulator(getInstance());
		verifierPanel = new Verifier(getInstance());

		mainTab = new JTabbedPane();
		mainTab.add("Editeur", this);
		mainTab.add("Simulateur", simulatorPanel);
		mainTab.add("Vérificateur", verifierPanel);
		mainTab.setBorder(new EmptyBorder(10, 0, 0, 0));
		mainTab.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));

		mainTab.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent event) {
				if (mainTab.getSelectedIndex() != 0) {
					wantedTabIndex = mainTab.getSelectedIndex();

					// Continue unless there is a currentFile and no modifications
					if (currentFile != null && !isModified()) {
						compileProject();
					} else {
						UIManager.put("OptionPane.messageFont", new Font("Ubuntu Mono", Font.PLAIN, 14));
						UIManager.put("OptionPane.buttonFont", new Font("Ubuntu Mono", Font.PLAIN, 14));

						mainTab.setSelectedIndex(0);
						int response = JOptionPane.showConfirmDialog(graphComponent,
								"Vous devez d'abord sauvegarder l'automate courant afin de continuer ! Proccéder ?",
								"Attention", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
								new ImageIcon(App.class.getResource("/about.png")));

						if (response == JOptionPane.OK_OPTION) {
							// Perform save of the graph
							String filename = null;

							if (currentFile == null) {
								String wd = System.getProperty("user.dir");

								JFileChooser fc = new JFileChooser(wd);
								EditorFileFilter xmlFilter = new EditorFileFilter(".xml", "Fichier XML");
								fc.setAcceptAllFileFilterUsed(false);
								fc.setFileFilter(xmlFilter);
								setFileChooserFont(fc.getComponents());

								if (fc.showDialog(null, "Enregistrer l'automate") != JFileChooser.APPROVE_OPTION) {
									return;
								}

								filename = fc.getSelectedFile().getAbsolutePath();

								if (!filename.toLowerCase().endsWith(xmlFilter.getExtension())) {
									filename += xmlFilter.getExtension();
								}

								if (new File(filename).exists() && JOptionPane.showConfirmDialog(graphComponent,
										"Fusionner avec le fichier existant ?") != JOptionPane.YES_OPTION) {
									return;
								}
							} else {
								filename = currentFile.getAbsolutePath();
							}

							// Write in the file
							try {
								XmlHandler xmlHandler = new XmlHandler(getInstance());
								mxUtils.writeFile(xmlHandler.getAsXml((mxGraph) getGraphComponent().getGraph()),
										filename);
								status("Fichier sauvegardé avec succès");

								setModified(false);
								setCurrentFile(new File(filename));
								compileProject();
							} catch (Throwable exception) {
								exception.printStackTrace();
								JOptionPane.showMessageDialog(graphComponent, exception.toString(), "Erreur",
										JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
			}
		});

		mainFrame.getContentPane().add(mainTab);

		return mainFrame;
	}

	public JPanel createNavComponent() {
		JPanel nav = new JPanel();
		nav.setLayout(new BorderLayout(5, 5));

		JLabel title = new JLabel("Déclarations globales");
		title.setBorder(new EmptyBorder(10, 10, 10, 10));
		title.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
		nav.add(title, BorderLayout.NORTH);

		this.area = new JTextArea();
		area.setBorder(new EmptyBorder(5, 5, 5, 5));
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
		JScrollPane scrollArea = new JScrollPane(area);
		scrollArea.setBorder(new EmptyBorder(3, 3, 3, 3));
		nav.add(scrollArea, BorderLayout.CENTER);

		area.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				//
			}

			@Override
			public void keyReleased(KeyEvent e) {
				globalDeclarations = area.getText();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				//
			}
		});

		return nav;
	}

	public void updateGraph(final mxGraph newGraph) {
		this.getUndoManager().clear();
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
		this.setModified(false);
	}

	protected void installToolBar() {
		this.add(new MeToolBar(this, JToolBar.HORIZONTAL), BorderLayout.NORTH);
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
		if (this.mainFrame != null) {
			this.mainFrame.dispose();
		}
	}

	public void updateTitle() {
		if (this.mainFrame != null) {
			String title = (currentFile != null) ? currentFile.getAbsolutePath() : "Nouvel Automate";

			if (modified) {
				title += "*";
			}

			this.mainFrame.setTitle(this.appTitle + " - " + title);
		}
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(JFrame mainFrame) {
		this.mainFrame = mainFrame;
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

	protected JLabel createStatusBar() {
		JLabel statusBar = new JLabel("Prêt");
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		statusBar.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
		return statusBar;
	}

	public void status(String msg) {
		statusBar.setText(msg);
	}

	public File getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(File file) {
		File oldValue = currentFile;
		currentFile = file;

		firePropertyChange("currentFile", oldValue, file);

		if (oldValue != file) {
			updateTitle();
		}
	}

	protected mxUndoManager createUndoManager() {
		return new mxUndoManager();
	}

	public mxUndoManager getUndoManager() {
		return undoManager;
	}

	public mxGraphComponent getGraphComponent() {
		return graphComponent;
	}

	public void setGraphComponent(mxGraphComponent graphComponent) {
		this.graphComponent = graphComponent;
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

	public boolean isLoading() {
		return loading;
	}

	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	public String getGlobalDeclarations() {
		return globalDeclarations;
	}

	public void setGlobalDeclarations(String globalDeclarations) {
		this.area.setText(globalDeclarations);
		this.globalDeclarations = globalDeclarations;
	}

	public Console createConsolePanel() {
		return new Console();
	}

	public Console getConsolePanel() {
		return this.consolePanel;
	}

	public void compileProject() {
		long compilationStartTime = System.currentTimeMillis();

		try {
			// FETCH DIFFERENTS TOKENS
			consolePanel.printSuccess("Récupération des tokens...\n");
			HashMap<Compilables, ArrayList<String>> elements = XmlHandler.getCompilables(currentFile);
			COMPILABLES_ELEMENTS = elements;
			long step1Time = System.currentTimeMillis();
			consolePanel.success("\nTerminé en " + (step1Time - compilationStartTime) + "ms");

			// GENERATE COMPILABLE FILE
			File file = getCompilablesFile(elements);

			// PROCEED TO PARSER WITH THE GENERATED FILE
			FinalProgram = Compiler.testSementic(file, consolePanel);

			removeCurrentTempFile();

			if (wantedTabIndex != 0)
				mainTab.setSelectedIndex(wantedTabIndex);
		} catch (IOException e) {
			consolePanel.printError(e.getMessage());
		}
	}

	public File getCompilablesFile(HashMap<Compilables, ArrayList<String>> elements) {
		String fullProgram = "int main(){\n";

		for (String dec : elements.get(Compilables.DECLARATIONS)) {
			fullProgram += dec;
		}

		for (String inv : elements.get(Compilables.INVARIANTS)) {
			fullProgram += "\ninv " + inv + ";";
		}

		for (String guard : elements.get(Compilables.CONDITIONS)) {
			String condition = "", update = "";
			String[] tokens = guard.split("\\^");

			if (tokens.length == 1) {
				condition = tokens[0];
			} else if (tokens.length == 2) {
				condition = tokens[0];
				update = tokens[1];
			}

			fullProgram += "\nif (" + condition + ") { " + update + " }";
		}

		fullProgram += "\n}";

		System.out.println(fullProgram);

		File outputFile = createCurrentTempFile();
		try {
			FileWriter writer = new FileWriter(outputFile);
			writer.write(fullProgram);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return currentTempFile;
	}

	public static File createCurrentTempFile() {
		File outputFile = new File("temp/" + String.valueOf(System.currentTimeMillis()) + ".txt");

		try {
			if (outputFile.createNewFile()) {
				currentTempFile = outputFile;
			} else {
				currentTempFile = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return currentTempFile;
	}

	public static void removeCurrentTempFile() throws IOException {
		if (currentTempFile != null) {
			if (currentTempFile.delete())
				currentTempFile = null;
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
