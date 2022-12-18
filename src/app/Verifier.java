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
import utils.UppaalXmlHandler;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
					statusVerifier.normal("Analyze en cours....");
//					verifierAnalyzer.analyze(propertie.getText());
					try {
						String pathModel = "temp_uppaal/automata.xml";
						String requestFile = "temp_uppaal/automata.q";
						UppaalXmlHandler uppaalXmlHandler = new UppaalXmlHandler(app.getAutomata(),pathModel,app.getEvents());
						uppaalXmlHandler.write();
						System.out.println("EXPRESSION UPPAAL = "+propertie.getText());
						uppaalXmlHandler.createCurrentTempFile(propertie.getText());

						Process proc = Runtime.getRuntime().exec("uppaal/bin-Linux/verifyta  -t1  "+pathModel+" "+requestFile);
						BufferedReader stdInput = new BufferedReader(new
								InputStreamReader(proc.getInputStream()));

						BufferedReader stdError = new BufferedReader(new
								InputStreamReader(proc.getErrorStream()));


						// Read the output from the command
						System.out.println("Here is the standard output of the command:\n");
						String s = null;


						while ((s = stdInput.readLine()) != null) {
							System.out.println(s);
							if(!s.isEmpty()) {

								if(s.contains("Property is NOT satisfied.")) {
									statusVerifier.error("La propriété n'est PAS satisfaite.");
								} else if (s.contains("Property is satisfied")) {
									statusVerifier.success("La propriété est satisfaite");
								}


//								statusVerifier.success(s);

							}

						}


						// Read any errors from the attempted command
						System.out.println("Here is the standard error of the command (if any):\n");
						while ((s = stdError.readLine()) != null) {
							if(s.contains("automata has no member named")) {
								statusVerifier.error("Une erreur s'est produite. Veuillez vérifier si les états sont existants");
							} else if(s.contains("syntax error")) {
								statusVerifier.error("Une erreur de syntaxe s'est produite. Vérifier l'exactitude de la proprieté");
							} else if(s.contains("type error")) {
								statusVerifier.error("Une erreur de type s'est produite. Vérifier l'exactitude de la proprieté");
							}
//							statusVerifier.error(s);
							System.out.println(s);

						}




					}catch (IOException e) {
						e.printStackTrace();
					}




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
