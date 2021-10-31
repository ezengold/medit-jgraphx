package app;

import ui.CustomTableRenderer;
import ui.ModelRequest;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class StatusVerifier extends JPanel {
    Object[][] statusData = {};
    String title[] = { "Status" };
    ModelRequest modelRequest = new ModelRequest(statusData, title);
    CustomTableRenderer colouringTable = new CustomTableRenderer();
    JTable tableRequest = new JTable(modelRequest){
        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component c = super.prepareRenderer(renderer, row, column);
            JComponent jc = (JComponent)c;

            if (isRowSelected(row)){
                jc.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, this.getSelectionBackground()));
            }
            else
                jc.setBorder(null);

            return c;
        }



    };

    public void clearStatus() {
        int rowCount = modelRequest.getRowCount();
        //Remove rows one by one from the end of the table
        if(rowCount>1) {
            for (int i = rowCount - 1; i >= 1; i--) {
                modelRequest.removeRow(i);
            }
            colouringTable.removeColors();
        }
    }


    public void normal(String msg){
        modelRequest.addRow(new Object[]{msg});
        colouringTable.setColors(Color.black);
        System.out.println(msg);
    }

    public void error(String msg) {
        modelRequest.addRow(new Object[] {msg});
        System.out.println(msg);
        colouringTable.setColors(Color.decode("#f90700"));
    }

    public void success(String msg) {

        modelRequest.addRow(new Object[] {msg});
        System.out.println(msg);
        colouringTable.setColors(Color.decode("#00b44f"));
    }


    public StatusVerifier() {
        this.setLayout(new BorderLayout(0, 0));
        JLabel statusLabel = new JLabel("Status");
        statusLabel.setFont(new Font("Times New Roman", Font.BOLD, 13));
        JPanel statusContent = new JPanel();
//		statusContent.setBackground(Color.white);
//		statusContent.setPreferredSize(new Dimension(1000,100));

        tableRequest.setPreferredSize(this.getPreferredSize());
        tableRequest.setRowHeight(20);
        tableRequest.setShowGrid(false);
        tableRequest.setTableHeader(null);
        tableRequest.setShowVerticalLines(false);
        tableRequest.setRowSelectionAllowed(false);
        tableRequest.getColumn("Status").setCellRenderer(colouringTable);
        normal("(Model Checked) Media Graphx v1");


        JScrollPane scrollStatus = new JScrollPane(tableRequest);
        scrollStatus.setPreferredSize(new Dimension(this.getWidth(), 200));
        statusContent.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray));
        statusContent.setLayout(new GridLayout(1,1));
        statusContent.add(scrollStatus);

        statusContent.add(scrollStatus);
        this.add(statusLabel, BorderLayout.NORTH);
        this.add(statusContent, BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 10));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.getWidth(),300);
    }
}
