package app;

import java.awt.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.*;
import com.mxgraph.util.*;
import com.mxgraph.view.mxGraph;

import models.Automata;
import models.Notification;
import models.Notification.Name;
import utils.Compiler;
import utils.Observer;
import utils.XmlHandler;
import verifier.ast.Program;
import verifier.semantic.MeSemanticAnalyzer;
import ui.MenuBar;
import ui.SystemTree;

public class App extends JPanel {

	public enum Compilables {
		DECLARATIONS, IDENTIFIERS, CONDITIONS, UPDATES, INVARIANTS
	}

	private static final long serialVersionUID = -8150527452734294724L;

	/*
	 * TODO Remove this because we are switching to templates now
	 * Hold the current automata model
	 */
	protected Automata automata;

	/*
	 * Hold the system automatas models
	 */
	protected ArrayList<Automata> templates = new ArrayList<Automata>();

	/*
	 * Main app frame
	 */
	protected JFrame mainFrame;

	/*
	 * Main tab container
	 */
	protected JTabbedPane mainTab;

	/*
	 * Wanted tab container to reach
	 */
	protected int wantedTabIndex = 0;

	/*
	 * Editor panel
	 */
	protected Editor editorPanel;

	/**
	 * System declaration tree
	 */
	protected SystemTree systemTree;

	/*
	 * Center panel containing either global declarations
	 * or a template editor
	 */
	protected JPanel centerPanel;

	/*
	 * Console panel to view errors and status
	 */
	protected Console consolePanel;

	/*
	 * Simulator panel
	 */
	protected Simulator simulatorPanel;

	/*
	 * Verifier panel
	 */
	protected Verifier verifierPanel;

	/*
	 * Wrapper for editor
	 */
	protected JPanel editorWrapper;

	/*
	 * Wrapper for textarea
	 */
	protected JPanel decsWrapper;

	/*
	 * Input holding the global declarations
	 */
	protected JTextArea area = new JTextArea();

	/*
	 * Global declarations
	 */
	protected String globalDeclarations = "";

	/**
	 * Title of the Application
	 */
	protected String appTitle;

	/*
	 * Label displayed at the bottom to show the status of the Application
	 */
	protected JLabel statusBar;

	/*
	 * Current xml file where the changes are saved to
	 */
	protected File currentFile;

	/*
	 * An automate file
	 */
	public static File automateFile;

	/*
	 * Temporary file used to store compiled code
	 */
	public static File currentTempFile;

	/*
	 * Final program after semantic analyze
	 */
	public static Program FinalProgram;

	/**
	 * Compilables elements contains ids, invariants, declarations and conditions...
	 */
	public static HashMap<Compilables, ArrayList<String>> COMPILABLES_ELEMENTS;

	/**
	 * Flag indicating whether the current graph has been modified
	 */
	protected boolean modified = false;

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

				app.createFrame(new MenuBar(app)).setVisible(true);
			}
		});
	}

	public App() {
		this.appTitle = "Medit";

		this.templates.add(new Automata("Automata 1"));

		JPanel systemTreePanel = createSystemTree();

		this.decsWrapper = createOrRefreshCenterPanel(0);

		this.editorWrapper = createOrRefreshCenterPanel(1);

		this.centerPanel = new JPanel();
		centerPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

		centerPanel.setLayout(new CardLayout());

		centerPanel.add("0", this.decsWrapper);
		centerPanel.add("1", this.editorWrapper);

		this.consolePanel = createConsolePanel();

		JSplitPane leftInner = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, systemTreePanel, this.centerPanel);
		leftInner.setDividerLocation(250);
		leftInner.setResizeWeight(1);
		leftInner.setDividerSize(5);
		leftInner.setBorder(null);

		JSplitPane mainSpliter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftInner, consolePanel);
		mainSpliter.setDividerLocation(900);
		mainSpliter.setResizeWeight(1);
		mainSpliter.setDividerSize(5);
		mainSpliter.setBorder(null);

		// Creates the status bar
		statusBar = createStatusBar();

		setLayout(new BorderLayout());

		add(mainSpliter, BorderLayout.CENTER);

		add(statusBar, BorderLayout.SOUTH);

		updateTitle();
	}

	public mxGraphComponent getGraphComponent() {
		return new mxGraphComponent(new mxGraph());
	}

	public App getInstance() {
		return this;
	}

	public JFrame createFrame(MenuBar menuBar) {
		this.mainFrame = new JFrame(this.appTitle);

		mainFrame.setLocationRelativeTo(null);
		mainFrame.setSize(1350, 750);
		mainFrame.setIconImage((new ImageIcon(getClass().getResource("/location.png")).getImage()));
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setJMenuBar(menuBar);

		this.updateTitle();

		// Create other tabs instances and forward the graph context
		verifierPanel = new Verifier(getInstance());

		mainTab = new JTabbedPane();

		mainTab.add("Editeur", this);

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
						// do stuff refer to remote repository
					}
				}
			}
		});

		mainFrame.getContentPane().add(mainTab);

		return mainFrame;
	}

	public JPanel createSystemTree() {
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(true);
		header.setBackground(new Color(0, 0, 0, 20));
		header.setBorder(new EmptyBorder(10, 10, 10, 10));
		JButton addBtn = new JButton("Template", new ImageIcon(getClass().getResource("/plus.png")));
		addBtn.setOpaque(true);
		addBtn.setBorderPainted(false);
		addBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// create and call addTemplate method
			}
		});
		header.add(addBtn, BorderLayout.EAST);

		JPanel nav = new JPanel();

		nav.setLayout(new BorderLayout());
		nav.setBorder(new CompoundBorder(BorderFactory.createTitledBorder(" Description du système "),
				new EmptyBorder(0, 5, 5, 5)));

		this.systemTree = new SystemTree(this.templates);
		systemTree.setBorder(new EmptyBorder(5, 5, 5, 5));

		JScrollPane scrollView = new JScrollPane(systemTree);
		scrollView.setBorder(new EmptyBorder(5, 5, 5, 5));

		nav.add(header, BorderLayout.NORTH);
		nav.add(scrollView, BorderLayout.CENTER);

		systemTree.addObserver(new Observer() {
			@Override
			public void update(Object data) {
				if (data instanceof Notification) {
					Notification notification = (Notification) data;

					if (notification.name == Name.SYSTEM_LINE_CLICKED) {
						int wantedIndex = (int) notification.object;

						CardLayout layout = (CardLayout) centerPanel.getLayout();

						if (wantedIndex == 0) {
							decsWrapper = createOrRefreshCenterPanel(0);
							layout.show(centerPanel, "0");
						} else {
							editorWrapper = createOrRefreshCenterPanel(1);
							layout.show(centerPanel, "1");
						}
					}
				}
			}
		});

		return nav;
	}

	/*
	 * Create the center panel according to the tree branch selected
	 * index = 0 for global declarations
	 */
	public JPanel createOrRefreshCenterPanel(int index) {
		if (index == 0) {
			return createGlobalDeclarationsPanel();
		} else if (index > 0 && index <= this.templates.size()) {
			return createAnEditorPanel(index);
		}
		return new JPanel();
	}

	public JPanel createGlobalDeclarationsPanel() {
		JPanel nav = new JPanel();

		nav.setLayout(new BorderLayout());
		nav.setBorder(new CompoundBorder(BorderFactory.createTitledBorder(" Déclarations globales "),
				new EmptyBorder(0, 5, 5, 5)));

		this.area = new JTextArea();
		area.setBorder(new EmptyBorder(5, 5, 5, 5));
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
		JScrollPane scrollArea = new JScrollPane(area);
		scrollArea.setBorder(new EmptyBorder(5, 5, 5, 5));
		nav.add(scrollArea, BorderLayout.CENTER);

		area.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				//
			}

			@Override
			public void keyReleased(KeyEvent e) {
				globalDeclarations = area.getText();
				setModified(true);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				//
			}
		});

		return nav;
	}

	public JPanel createAnEditorPanel(int index) {
		JPanel nav = new JPanel();

		nav.setLayout(new BorderLayout());
		nav.setBorder(new EmptyBorder(0, 5, 0, 5));

		Automata activeAutomata = this.templates.get(index - 1);

		this.editorPanel = new Editor(activeAutomata);

		editorPanel.addObserver(new Observer() {
			@Override
			public void update(Object data) {
				if (data instanceof Notification) {
					Notification notification = (Notification) data;

					switch (notification.name) {
						case GRAPH_MODIFIED:
							setModified((Boolean) notification.object);
							break;

						case STATUS_MESSAGE:
							status((String) notification.object);
							break;

						default:
							break;
					}
				}
			}
		});

		nav.add(this.editorPanel, BorderLayout.CENTER);

		return nav;
	}

	public void updateGraph(final mxGraph newGraph) {
		//
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

	public Automata getAutomata() {
		return automata;
	}

	public void setAutomata(Automata automata) {
		this.automata = automata;
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
			MeSemanticAnalyzer analyze = Compiler.testSementic(file, consolePanel, automata);

			FinalProgram = analyze.getFinalProgram();

			// automata.getEngine().debug();

			// removeCurrentTempFile();

			if (analyze.getErrors() < 1) {
				if (wantedTabIndex != 0) {
					mainTab.setSelectedIndex(wantedTabIndex);

					if (wantedTabIndex == 1) {
						refreshSimulator();
					}
				}
			} else {
				mainTab.setSelectedIndex(0);
			}
		} catch (IOException e) {
			consolePanel.error(e.getMessage());
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

		System.out.println("FULL PROGRAM: " + fullProgram);

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

	public void refreshSimulator() {
		// simulatorPanel.recreateSimulatorGraph(graphComponent.getGraph());
		simulatorPanel.setCurrentState(automata.getInitialState());
		simulatorPanel.getTracesTableModel().removeAllTraces();
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
	public Action bind(String name, final Action action, String iconUrl) {
		AbstractAction newAction = new AbstractAction(name,
				(iconUrl != null) ? new ImageIcon(App.class.getResource(iconUrl)) : null) {
			public void actionPerformed(ActionEvent e) {
				action.actionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand())); // TODO refer to remote repo
			}
		};

		newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));

		return newAction;
	}
}
