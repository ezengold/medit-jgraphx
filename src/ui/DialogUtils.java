package ui;

import javax.swing.*;

public class DialogUtils {

    //error dialog
    public static void errorDialog(String msg) {
        JOptionPane.showMessageDialog(null,msg,"Erreur",JOptionPane.ERROR_MESSAGE);
    }

    //information dialog
    public static void informationDialog(String msg) {
        JOptionPane.showMessageDialog(null,msg,"Information",JOptionPane.INFORMATION_MESSAGE);
    }



}
