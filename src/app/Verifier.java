package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import com.mxgraph.swing.mxGraphComponent;

import java.awt.*;

public class Verifier extends JPanel {

	private static final long serialVersionUID = -6129589946079881207L;

	private JPanel topContainer = new JPanel();
	private JPanel middleContainer = new JPanel();
	private JPanel bottomContainer = new JPanel();

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
		inserer.setBackground(Color.GRAY);
		JButton supprimer = new JButton("Supprimer");
		supprimer.setBackground(Color.GRAY);
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

		JTextArea propertie = new JTextArea("");
		propertie.setLineWrap(true);
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
		statusContent.setBackground(Color.white);
		statusContent.setPreferredSize(new Dimension(1000, 100));
		statusContent.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray));
		bottomContainer.add(statusLabel, BorderLayout.NORTH);
		bottomContainer.add(statusContent, BorderLayout.CENTER);
		bottomContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 10));

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
