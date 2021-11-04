package ui;

import javax.swing.table.DefaultTableModel;

public class TracesTableModel extends DefaultTableModel {

	public TracesTableModel(Object[][] data, String[] columns) {
		super(data, columns);
	}

	public void addTrace(String trace) {
		insertRow(0, new Object[] { trace });
	}
}
