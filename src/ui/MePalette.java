package ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxRectangle;

public class MePalette extends JPanel {

	private static final long serialVersionUID = 7741260259530233593L;

	protected JLabel selectedEntry = null;

	protected mxEventSource eventSource = new mxEventSource(this);

	public MePalette() {
		setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));
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

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
			 */
			public void mousePressed(MouseEvent e) {
				setSelectionEntry(entry, t);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent e) {
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
			 */
			public void mouseEntered(MouseEvent e) {
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
			 */
			public void mouseExited(MouseEvent e) {
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
			 */
			public void mouseReleased(MouseEvent e) {
			}

		});

		// Install the handler for dragging nodes into a graph
		DragGestureListener dragGestureListener = new DragGestureListener() {
			/**
			 * 
			 */
			public void dragGestureRecognized(DragGestureEvent e) {
				e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(), t, null);
			}

		};

		DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(entry, DnDConstants.ACTION_COPY, dragGestureListener);

		add(entry);
	}
}
