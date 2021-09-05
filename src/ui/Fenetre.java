package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Fenetre extends JFrame {
	private static final long serialVersionUID = 1L;

    private static final Dimension DEFAULT_SIZE = new Dimension(1200, 700);
    
	private JMenuBar mainMenuBar = new JMenuBar();

	private JToolBar mainToolBar = new JToolBar("Options");
	private JButton fileBtn;
	private JButton openBtn;
	private JButton saveBtn;
	private JButton cursorBtn;
	private JButton locationBtn;
	private JButton edgeBtn;

	private JMenu fichierMenu = new JMenu("Fichier");
	private JMenuItem newFileItem = new JMenuItem("Nouveau fichier");
	private JMenuItem openFileItem = new JMenuItem("Ouvrir un fichier");
	private JMenuItem saveFileItem = new JMenuItem("Sauvegarder");
	private JMenuItem closeFileItem = new JMenuItem("Fermer");
	private JMenuItem quitItem = new JMenuItem("Quitter");

	private JMenu optionsMenu = new JMenu("Options");
	private JMenuItem simulatorItem = new JMenuItem("Afficher le simulateur");

	private JMenu helpMenu = new JMenu("Aide");
	private JMenuItem aboutItem = new JMenuItem("A propos");

	private JPanel conteneur = new JPanel();
	private JSplitPane mainPanel;
	private Editor editorPanel;
	private JPanel terminalPanel = new JPanel();

	public Fenetre() {
		conteneur.setLayout(new BorderLayout());

		this.setTitle("Medit");
		this.setSize(DEFAULT_SIZE);
		this.setPreferredSize(new Dimension(1200, 700));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Fenetre.this.setVisible(false);
				Fenetre.this.dispose();
			}
		});
		this.setResizable(true);

		// START MENU BAR
		fichierMenu.add(newFileItem);
		fichierMenu.add(openFileItem);
		fichierMenu.add(saveFileItem);
		fichierMenu.add(closeFileItem);
		fichierMenu.add(quitItem);
		mainMenuBar.add(fichierMenu);

		optionsMenu.add(simulatorItem);
		mainMenuBar.add(optionsMenu);

		helpMenu.add(aboutItem);
		mainMenuBar.add(helpMenu);
		this.setJMenuBar(mainMenuBar);
		// END MENU BAR

		editorPanel = new Editor(conteneur);
		editorPanel.setSize((int) (conteneur.getWidth() * 0.6), conteneur.getHeight());
		terminalPanel.setBackground(Color.DARK_GRAY);

		mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorPanel, terminalPanel);
		mainPanel.setDividerSize(2);
		mainPanel.setDividerLocation(900);
		conteneur.add(mainPanel, BorderLayout.CENTER);
		this.setContentPane(conteneur);

		this.createToolBar();

		quitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
	}

	public void createToolBar() {
		fileBtn = new Button("Nouveau", "New", "Nouveau fichier", "/file.png");
		openBtn = new Button("Ouvrir", "Open", "Ouvrir un fichier", "/folder.png");
		saveBtn = new Button("Enregistrer", "Save", "Enregistrer le fichier", "/disk.png");
		cursorBtn = new Button("Sélecteur", "Cursor", "Sélecteur par défaut", "/cursor.png");
		locationBtn = new Button("État", "State", "Ajouter un état", "/location.png");
		edgeBtn = new Button("Transition", "Edge", "Ajouter une transition", "/edge.png");

		mainToolBar.add(fileBtn);
		mainToolBar.add(openBtn);
		mainToolBar.add(saveBtn);
		mainToolBar.addSeparator(new Dimension(30, 0));
		mainToolBar.add(cursorBtn);
		mainToolBar.add(locationBtn);
		mainToolBar.add(edgeBtn);

		mainToolBar.setFloatable(false);
		mainToolBar.setBorder(new EmptyBorder(5, 5, 5, 5));
		conteneur.add(mainToolBar, BorderLayout.PAGE_START);
	}

	public JMenuBar getMainMenuBar() {
		return mainMenuBar;
	}

	public void setMainMenuBar(JMenuBar mainMenuBar) {
		this.mainMenuBar = mainMenuBar;
	}

	public JMenu getFichierMenu() {
		return fichierMenu;
	}

	public void setFichierMenu(JMenu fichierMenu) {
		this.fichierMenu = fichierMenu;
	}

	public JMenuItem getNewFileItem() {
		return newFileItem;
	}

	public JToolBar getMainToolBar() {
		return mainToolBar;
	}

	public void setMainToolBar(JToolBar mainToolBar) {
		this.mainToolBar = mainToolBar;
	}

	public JButton getFileBtn() {
		return fileBtn;
	}

	public void setFileBtn(JButton fileBtn) {
		this.fileBtn = fileBtn;
	}

	public JButton getOpenBtn() {
		return openBtn;
	}

	public void setOpenBtn(JButton openBtn) {
		this.openBtn = openBtn;
	}

	public JButton getSaveBtn() {
		return saveBtn;
	}

	public void setSaveBtn(JButton saveBtn) {
		this.saveBtn = saveBtn;
	}

	public JButton getCursorBtn() {
		return cursorBtn;
	}

	public void setCursorBtn(JButton cursorBtn) {
		this.cursorBtn = cursorBtn;
	}

	public JButton getLocationBtn() {
		return locationBtn;
	}

	public void setLocationBtn(JButton locationBtn) {
		this.locationBtn = locationBtn;
	}

	public JButton getEdgeBtn() {
		return edgeBtn;
	}

	public void setEdgeBtn(JButton edgeBtn) {
		this.edgeBtn = edgeBtn;
	}

	public void setNewFileItem(JMenuItem newFileItem) {
		this.newFileItem = newFileItem;
	}

	public JMenuItem getOpenFileItem() {
		return openFileItem;
	}

	public void setOpenFileItem(JMenuItem openFileItem) {
		this.openFileItem = openFileItem;
	}

	public JMenuItem getSaveFileItem() {
		return saveFileItem;
	}

	public void setSaveFileItem(JMenuItem saveFileItem) {
		this.saveFileItem = saveFileItem;
	}

	public JMenuItem getCloseFileItem() {
		return closeFileItem;
	}

	public void setCloseFileItem(JMenuItem closeFileItem) {
		this.closeFileItem = closeFileItem;
	}

	public JMenuItem getQuitItem() {
		return quitItem;
	}

	public void setQuitItem(JMenuItem quitItem) {
		this.quitItem = quitItem;
	}

	public JMenu getOptionsMenu() {
		return optionsMenu;
	}

	public void setOptionsMenu(JMenu optionsMenu) {
		this.optionsMenu = optionsMenu;
	}

	public JMenuItem getSimulatorItem() {
		return simulatorItem;
	}

	public void setSimulatorItem(JMenuItem simulatorItem) {
		this.simulatorItem = simulatorItem;
	}

	public JMenu getHelpMenu() {
		return helpMenu;
	}

	public void setHelpMenu(JMenu helpMenu) {
		this.helpMenu = helpMenu;
	}

	public JMenuItem getAboutItem() {
		return aboutItem;
	}

	public void setAboutItem(JMenuItem aboutItem) {
		this.aboutItem = aboutItem;
	}

	public JPanel getConteneur() {
		return conteneur;
	}

	public void setConteneur(JPanel conteneur) {
		this.conteneur = conteneur;
	}

	public JSplitPane getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(JSplitPane mainPanel) {
		this.mainPanel = mainPanel;
	}

	public Editor getEditorPanel() {
		return editorPanel;
	}

	public void setEditorPanel(Editor editorPanel) {
		this.editorPanel = editorPanel;
	}

	public JPanel getTerminalPanel() {
		return terminalPanel;
	}

	public void setTerminalPanel(JPanel terminalPanel) {
		this.terminalPanel = terminalPanel;
	}
}
