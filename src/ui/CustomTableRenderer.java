package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class CustomTableRenderer extends DefaultTableCellRenderer {
    JLabel label = new JLabel();

    private ArrayList<Color> desiredColors = new ArrayList<Color>();

    public void setColors(Color incomingColor)
    {
        desiredColors.add(incomingColor);
    }

    public void removeColors() {
        desiredColors = new ArrayList<>();
        desiredColors.add(Color.black);

    }


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        cellComponent.setFont(new Font("Times New Roman", Font.BOLD, 13));
        for (int i = 0; i < desiredColors.size(); i++) {
            if(row == i){
                cellComponent.setForeground(desiredColors.get(i));
            }
        }
        return cellComponent;
    }
}
