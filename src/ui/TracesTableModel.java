package ui;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class TracesTableModel extends DefaultTableModel {

	public TracesTableModel(String columnName) {
		super();
		addColumn(columnName, new Vector<Object>(Arrays.asList("")));
	}

	public void addTrace(String traceId) {
		insertRow(0, new Object[] { traceId });
	}

	public void removeAllTraces() {
		getDataVector().removeAllElements();
		fireTableDataChanged();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
