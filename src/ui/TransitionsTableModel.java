package ui;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class TransitionsTableModel extends DefaultTableModel {

	public TransitionsTableModel(String columnName) {
		super();
		addColumn(columnName, new Vector<Object>(Arrays.asList("")));
	}

	public void addTransition(String transitionId) {
		insertRow(0, new Object[] { transitionId });
	}

	public void removeAllTransitions() {
		getDataVector().removeAllElements();
		fireTableDataChanged();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
