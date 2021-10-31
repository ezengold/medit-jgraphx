package ui;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

public class ModelRequest extends DefaultTableModel {
    private Object[][] data;
    private String[] title;

    public ModelRequest(Object[][] data,String[]title) {
        super(data,title);
        this.data = data;
        this.title = title;

    }





    @Override
    public int getRowCount() {
        return super.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int i, int i1) {
        return super.getValueAt(i, i1);
    }

    @Override
    public String getColumnName(int i) {
        return super.getColumnName(i);
    }

    

    @Override
    public boolean isCellEditable(int i, int i1) {
        return false;
    }
}
