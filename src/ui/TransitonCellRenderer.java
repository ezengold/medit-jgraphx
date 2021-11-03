package ui;

import java.awt.*;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class TransitonCellRenderer extends Button implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		setHorizontalAlignment(SwingConstants.LEFT);
		setText(value != null && !((String) value.toString()).isEmpty() ? value.toString() : "Aucune trace");
		setBackground(isSelected ? Color.decode("#9099ae") : new Color(0, 0, 0, 0));

		return this;
	}

}
