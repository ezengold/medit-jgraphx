package ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;

import models.State;
import models.Transition;

public class MePalette extends JPanel {

	private static final long serialVersionUID = 7741260259530233593L;

	protected JLabel selectedEntry = null;

	protected mxEventSource eventSource = new mxEventSource(this);

	@SuppressWarnings("serial")
	public MePalette() {
		setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));

		// Add states tool template
		mxCell stateCell = new mxCell(new State(), new mxGeometry(0, 0, 20, 20), "");
		stateCell.setVertex(true);
		addTemplate("State", new ImageIcon(MePalette.class.getResource("/location.png")), stateCell);

		// Add edge tool template
		mxGeometry edgeGeometry = new mxGeometry(0, 0, 50, 50);
		edgeGeometry.setTerminalPoint(new mxPoint(0, 50), true);
		edgeGeometry.setTerminalPoint(new mxPoint(50, 0), false);
		edgeGeometry.setRelative(true);
		mxCell edgeCell = new mxCell(new Transition(), edgeGeometry, "");
		edgeCell.setEdge(true);
		addTemplate("Transition", new ImageIcon(MePalette.class.getResource("/edge.png")), edgeCell);

		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				clearSelection();
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		setTransferHandler(new TransferHandler() {
			public boolean canImport(JComponent comp, DataFlavor[] flavors) {
				return true;
			}
		});
	}

	public void clearSelection() {
		setSelectionEntry(null, null);
	}

	public void setSelectionEntry(JLabel entry, mxGraphTransferable t) {
		JLabel previous = selectedEntry;
		selectedEntry = entry;

		if (previous != null) {
			previous.setBorder(null);
			previous.setOpaque(false);
		}

		if (selectedEntry != null) {
			// selectedEntry.setBorder();
			selectedEntry.setOpaque(true);
		}

		eventSource.fireEvent(
				new mxEventObject(mxEvent.SELECT, "entry", selectedEntry, "transferable", t, "previous", previous));
	}

	/**
	 * Used to add cell template to palette
	 * 
	 * @param name
	 * @param icon
	 * @param cell
	 */
	public void addTemplate(final String name, ImageIcon icon, mxCell cell) {
		mxRectangle bounds = (mxGeometry) cell.getGeometry().clone();
		final mxGraphTransferable t = new mxGraphTransferable(new Object[] { cell }, bounds);

		// Scales the image if it's too large for the library
		if (icon != null) {
			if (icon.getIconWidth() > 32 || icon.getIconHeight() > 32) {
				icon = new ImageIcon(icon.getImage().getScaledInstance(32, 32, 0));
			}
		}

		final JLabel entry = new JLabel(icon);
		entry.setPreferredSize(new Dimension(50, 50));
		entry.setBackground(MePalette.this.getBackground().brighter());
		entry.setFont(new Font(entry.getFont().getFamily(), 0, 10));

		entry.setVerticalTextPosition(JLabel.BOTTOM);
		entry.setHorizontalTextPosition(JLabel.CENTER);
		entry.setIconTextGap(0);

		entry.setToolTipText(name);
		entry.setText(name);

		entry.addMouseListener(new MouseListener() {

			public void mousePressed(MouseEvent e) {
				setSelectionEntry(entry, t);
			}

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

		});

		// Install the handler for dragging nodes into a graph
		DragGestureListener dragGestureListener = new DragGestureListener() {
			public void dragGestureRecognized(DragGestureEvent e) {
				e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(), t, null);
			}
		};

		DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(entry, DnDConstants.ACTION_COPY, dragGestureListener);

		add(entry);
	}

	/*
	 * Used to bind an event to the palette
	 */
	public void addListener(String eventName, mxIEventListener listener) {
		eventSource.addListener(eventName, listener);
	}

	/**
	 * Tell whether or not event are enabled for this palette
	 */
	public boolean isEventsEnabled() {
		return eventSource.isEventsEnabled();
	}

	/**
	 * 
	 */
	public void removeListener(mxIEventListener listener) {
		eventSource.removeListener(listener);
	}

	/*
	 * 
	 */
	public void removeListener(mxIEventListener listener, String eventName) {
		eventSource.removeListener(listener, eventName);
	}

	/*
	 * 
	 */
	public void setEventsEnabled(boolean eventsEnabled) {
		eventSource.setEventsEnabled(eventsEnabled);
	}
}
