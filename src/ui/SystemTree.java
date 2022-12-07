package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import models.Automata;
import models.Notification;
import utils.Observable;
import utils.Observer;

public class SystemTree extends JPanel implements Observable {
	/**
	 * List of observers to the object
	 */
	private ArrayList<Observer> observersList = new ArrayList<Observer>();

	private JTree tree;

	private ArrayList<Automata> templates = new ArrayList<Automata>();

	public SystemTree() {
		buildTree();
	}

	public SystemTree(ArrayList<Automata> templates) {
		this.templates = templates;
		buildTree();
	}

	public void buildTree() {
		removeAll();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Système");

		// global declarations branch
		DefaultMutableTreeNode globalDecsBranch = new DefaultMutableTreeNode("Déclarations globales");
		root.add(globalDecsBranch);

		// add templates branch
		DefaultMutableTreeNode templatesBranch = new DefaultMutableTreeNode("Templates");

		if (templates != null) {
			for (Automata automata : templates) {
				templatesBranch.add(new DefaultMutableTreeNode(automata.getName()));
			}
		}
		root.add(templatesBranch);

		this.tree = new JTree(root);
		tree.setBorder(new EmptyBorder(5, 10, 10, 10));
		tree.setExpandsSelectedPaths(true);

		// remove folder and file icon on tree
		DefaultTreeCellRenderer cellRenderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
		cellRenderer.setLeafIcon(null);
		cellRenderer.setClosedIcon(null);
		cellRenderer.setOpenIcon(null);
		cellRenderer.setDisabledIcon(null);

		setLayout(new BorderLayout());
		add(tree, BorderLayout.CENTER);
		revalidate();
		repaint();

		expandAllTree();

		installListeners();
	}

	public void installListeners() {
		if (tree != null) {
			tree.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int index = tree.getRowForLocation(e.getX(), e.getY());

					if (index == 1) {
						updateObservers(new Notification(Notification.Name.SYSTEM_LINE_CLICKED, 0));
					} else if (index > 2) {
						updateObservers(new Notification(Notification.Name.SYSTEM_LINE_CLICKED, index - 2));
					}
				}
			});
		}
	}

	public void recreateTree() {
		buildTree();
	}

	public void recreateTreeWith(ArrayList<Automata> templates) {
		this.templates = templates;
		buildTree();
	}

	private void expandAllTree() {
		if (tree != null) {
			for (int i = 0; i < tree.getRowCount(); i++) {
				tree.expandRow(i);
			}
		}
	}

	public JTree getTree() {
		return tree;
	}

	public void setTree(JTree tree) {
		this.tree = tree;
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
