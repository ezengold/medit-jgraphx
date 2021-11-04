package ui;

import javax.swing.table.DefaultTableModel;

public class TransitionsTableModel extends DefaultTableModel {

	public TransitionsTableModel(Object[][] data, String[] columns) {
		super(data, columns);
	}

	public void addTransition(String transitionId) {
		insertRow(0, new Object[] { transitionId });
	}

	public void removeAllTransitions() {
		getDataVector().removeAllElements();
		fireTableDataChanged();
	}
}
