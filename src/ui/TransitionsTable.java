package ui;

import java.awt.*;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class TransitionsTable extends JTable {
	private Object[][] data;

	private String[] columns;

	public TransitionsTable(Object[][] data, String[] columns) {
		super(data, columns);

		setData(data);
		setColumns(columns);

		setRowHeight(35);
		setTableHeader(null);
		// setSelectionBackground(Color.decode("#9099ae"));
		// setSelectionForeground(Color.BLACK);
		setFileChooserFont(getComponents());
		getColumn(columns[0]).setCellRenderer(new TransitionCellRenderer());
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

	public Object[][] getData() {
		return data;
	}

	public void setData(Object[][] data) {
		this.data = data;
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public class TransitionCellRenderer extends Button implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			setHorizontalAlignment(SwingConstants.LEFT);
			setText(value != null ? value.toString() : "-");
			setBackground(isSelected ? Color.decode("#9099ae") : new Color(0, 0, 0, 0));

			return this;
		}

	}
}
