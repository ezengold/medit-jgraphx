package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.mxgraph.swing.mxGraphComponent;

import models.State;
import models.Transition;
import org.jgrapht.graph.DefaultDirectedGraph;
import ui.ModelRequest;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

public class Verifier extends JPanel {

	private static final long serialVersionUID = -6129589946079881207L;

	private JPanel topContainer = new JPanel();
	private JPanel middleContainer = new JPanel();
	private JPanel bottomContainer = new JPanel();

	//table to view request
	Object[][] data = {{""}};
	//Les titres des colonnes
	String title[] = {"Requete"};
    ModelRequest modelRequest = new ModelRequest(data,title);
    JTable tableRequest = new JTable(modelRequest);


    //propertie jtextArea
	JTextArea propertie = new JTextArea("");


	public Verifier(DefaultDirectedGraph<State, Transition> graphData, mxGraphComponent graphComponent) {


	}
	public Verifier(mxGraphComponent graphComponent) {
		initComponent();
	}

	private void initComponent() {
//		this.setLayout(new BorderLayout());
		/*
		 * top component
		 */
		// apercu

		JLabel apercu = new JLabel("Apercu");
		apercu.setFont(new Font("Times New Roman", Font.BOLD, 13));
		JPanel contentApercu = new JPanel();
		contentApercu.setBackground(Color.white);
		contentApercu.setPreferredSize(new Dimension(1000,120));
		contentApercu.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.lightGray));
		//table to view dynamically request
		tableRequest.setRowSelectionInterval(0 ,0);
		tableRequest.setPreferredSize(new Dimension(1000,120));
		tableRequest.setShowGrid(false);
		tableRequest.setRowHeight(30);
//		tableRequest.setSelectionBackground(Color.GRAY);
		tableRequest.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent listSelectionEvent) {
				if (tableRequest.getSelectedRow() != -1) {
					propertie.setEditable(true);
					propertie.setText(tableRequest.getValueAt(tableRequest.getSelectedRow(), 0).toString());
				} else {
					propertie.setText("");
				}
			}
		});



        //Les données du tableau

        JScrollPane jScrollPane = new JScrollPane(tableRequest);
        jScrollPane.setPreferredSize(new Dimension(950,120));
        contentApercu.setLayout(new BorderLayout());
        contentApercu.add(jScrollPane,BorderLayout.WEST);







		contentApercu.setPreferredSize(new Dimension(1000, 100));
		contentApercu.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray));

		JPanel viewContainer = new JPanel(new BorderLayout(0, 0));
		viewContainer.add(apercu, BorderLayout.CENTER);
		viewContainer.add(contentApercu, BorderLayout.SOUTH);

		// actions button
		JButton verifier = new JButton("Verifier");
		verifier.setPreferredSize(new Dimension(150, 20));
		verifier.setBackground(Color.black);
		JButton inserer = new JButton("Inserer");
		inserer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				modelRequest.insertRow(0,new Object[]{""});
				tableRequest.setRowSelectionInterval(0,0);
				propertie.setText("");
			}
		});

		inserer.setBackground(Color.GRAY);
		JButton supprimer = new JButton("Supprimer");
		supprimer.setBackground(Color.GRAY);

		supprimer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				int rowSelected = tableRequest.getSelectedRow();
				if (rowSelected != -1) {
					modelRequest.removeRow(tableRequest.getSelectedRow());

					if (modelRequest.getRowCount() == 0) {
						propertie.setEditable(false);
					} else {
						if (rowSelected == 0) {
							tableRequest.setRowSelectionInterval(rowSelected ,0);
						} else {
							tableRequest.setRowSelectionInterval(rowSelected -1,0);
						}

					}
				}


			}
		});
		JPanel containerButton = new JPanel(new GridLayout(3,1));
		containerButton.add(verifier);
		containerButton.add(inserer);
		containerButton.add(supprimer);

		topContainer.setLayout(new BorderLayout());
		topContainer.add(viewContainer, BorderLayout.WEST);
		topContainer.add(containerButton, BorderLayout.EAST);
		topContainer.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 10));

		/*
		 * Middle component
		 */

		middleContainer.setLayout(new BorderLayout(0, 0));
		JLabel requete = new JLabel("Requete");
		requete.setFont(new Font("Times New Roman", Font.BOLD, 13));
		requete.setLayout(new BorderLayout());
		requete.setHorizontalAlignment(SwingConstants.LEFT);


		propertie.setLineWrap(true);
		propertie.setBorder(new MatteBorder(1,1,1,1,Color.lightGray));
		propertie.setPreferredSize(new Dimension(700,100));

		propertie.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent keyEvent) {
				if (tableRequest.getSelectedRow() != -1) {
					modelRequest.setValueAt(propertie.getText()+keyEvent.getKeyChar(),tableRequest.getSelectedRow(),0);
				}
			}

			@Override
			public void keyPressed(KeyEvent keyEvent) {

			}

			@Override
			public void keyReleased(KeyEvent keyEvent) {

			}
		});




		propertie.setBorder(new MatteBorder(1, 1, 1, 1, Color.lightGray));
		propertie.setPreferredSize(new Dimension(700, 100));

		middleContainer.setBorder(new EmptyBorder(5, 5, 10, 10));
		middleContainer.add(requete, BorderLayout.NORTH);
		middleContainer.add(new JScrollPane(propertie), BorderLayout.CENTER);

		/*
		 * bottom component
		 */
		bottomContainer.setLayout(new BorderLayout(0, 0));
		JLabel statusLabel = new JLabel("Status");
		statusLabel.setFont(new Font("Times New Roman", Font.BOLD, 13));
		JPanel statusContent = new JPanel();
//		statusContent.setBackground(Color.white);
//		statusContent.setPreferredSize(new Dimension(1000,100));
		JScrollPane scrollStatus = new JScrollPane(statusContent);
		scrollStatus.setPreferredSize(new Dimension(1000,200));
		statusContent.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.lightGray));
		bottomContainer.add(statusLabel,BorderLayout.NORTH);
		bottomContainer.add(scrollStatus,BorderLayout.CENTER);
		bottomContainer.setBorder(BorderFactory.createEmptyBorder(5,5,10,10));


		Box b = Box.createVerticalBox();
		b.add(topContainer);
		b.add(middleContainer);
		b.add(bottomContainer);
//		JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,b,bottomContainer);
		this.add(b);

//		this.add(middleContainer);
//		this.add(bottomContainer);

	}

}
