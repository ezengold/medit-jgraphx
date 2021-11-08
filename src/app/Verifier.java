package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ui.DialogUtils;
import ui.ModelRequest;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Verifier extends JPanel {

	private static final long serialVersionUID = -6129589946079881207L;

	private App app;
	
	private JPanel topContainer = new JPanel();
	private JPanel middleContainer = new JPanel();
	private StatusVerifier statusVerifier = new StatusVerifier();
	private VerifierAnalyzer verifierAnalyzer;

	// table to view request
	Object[][] data = { { "" } };

	// Les titres des colonnes
	String title[] = { "Requete" };
	ModelRequest modelRequest = new ModelRequest(data, title);
	JTable tableRequest = new JTable(modelRequest);

	// propertie jtextArea
	JTextArea propertie = new JTextArea("");

	public Verifier(App app) {
		this.app = app;
		verifierAnalyzer = new VerifierAnalyzer(statusVerifier,app);


		// app.FinalProgram gives the final program after compilation and null otherwise
		
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
		contentApercu.setPreferredSize(this.getPreferredSize());
		contentApercu.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray));
		// table to view dynamically request
		tableRequest.setRowSelectionInterval(0, 0);
		tableRequest.setPreferredSize(new Dimension(800, 120));
		tableRequest.setShowGrid(false);
		tableRequest.setRowHeight(30);
		tableRequest.setTableHeader(null);
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

		// Les données du tableau

		JScrollPane jScrollPane = new JScrollPane(tableRequest);
		jScrollPane.setPreferredSize(new Dimension(950, 120));
		contentApercu.setLayout(new BorderLayout());
		contentApercu.add(jScrollPane, BorderLayout.WEST);

		contentApercu.setPreferredSize(new Dimension(800, 100));
		contentApercu.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray));

		JPanel viewContainer = new JPanel(new BorderLayout(0, 0));
		viewContainer.add(apercu, BorderLayout.CENTER);
		viewContainer.add(contentApercu, BorderLayout.SOUTH);

		// actions button
		JButton verifier = new JButton("Verifier");
		verifier.setPreferredSize(new Dimension(150, 20));
		verifier.setBackground(Color.black);
		verifier.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if(propertie.isEnabled() && !propertie.getText().isEmpty()) {
					statusVerifier.clearStatus();
					statusVerifier.normal(propertie.getText());
					verifierAnalyzer.analyze(propertie.getText());

				} else {
					DialogUtils.errorDialog("Veuillez renseigner une proprieté");
				}
			}
		});




		JButton inserer = new JButton("Insérer");
		inserer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				modelRequest.insertRow(0, new Object[] { "" });
				tableRequest.setRowSelectionInterval(0, 0);
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
							tableRequest.setRowSelectionInterval(rowSelected, 0);
						} else {
							tableRequest.setRowSelectionInterval(rowSelected - 1, 0);
						}

					}
				}

			}
		});
		JPanel containerButton = new JPanel(new GridLayout(3, 1));
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
		propertie.setBorder(new MatteBorder(1, 1, 1, 1, Color.lightGray));
		propertie.setPreferredSize(new Dimension(700, 100));

		propertie.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent documentEvent) {
				if (tableRequest.getSelectedRow() != -1) {
					//set value at line selected; column 0
					modelRequest.setValueAt(propertie.getText() , tableRequest.getSelectedRow(),
							0);
				}

			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent) {
				if (tableRequest.getSelectedRow() != -1) {
					//set value at line selected; column 0
					modelRequest.setValueAt(propertie.getText() , tableRequest.getSelectedRow(),
							0);
				}
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent) {
				if (tableRequest.getSelectedRow() != -1) {
					//set value at line selected; column 0
					modelRequest.setValueAt(propertie.getText() , tableRequest.getSelectedRow(),
							0);
				}
			}
		});


		propertie.setBorder(new MatteBorder(1, 1, 1, 1, Color.lightGray));
		propertie.setPreferredSize(new Dimension(700, 100));

		middleContainer.setBorder(new EmptyBorder(5, 5, 10, 10));
		middleContainer.add(requete, BorderLayout.NORTH);
		middleContainer.add(new JScrollPane(propertie), BorderLayout.CENTER);



		Box b = Box.createVerticalBox();
		b.add(topContainer);
		b.add(middleContainer);
		b.add(statusVerifier);
		this.add(b);


	}

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

}
