package utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;

import app.App;

public class EditorActions {

	public static final App getApp(ActionEvent e) {
		if (e.getSource() instanceof Component) {
			Component component = (Component) e.getSource();

			while (component != null && !(component instanceof App)) {
				component = component.getParent();
			}

			return (App) component;
		}

		return null;
	}

	@SuppressWarnings("serial")
	public static class NewAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				System.out.println("NEW FILE CLICKED");
			}
		}

	}

	@SuppressWarnings("serial")
	public static class OpenAction extends AbstractAction {
		/*
		 * 
		 */
		protected String lastDir;

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				if (!app.isModified() || JOptionPane.showConfirmDialog(app,
						"Êtes-vous prêts à perdre vos modifications ?") == JOptionPane.YES_OPTION) {
					mxGraphComponent graphComponent = app.getGraphComponent();

					if (graphComponent != null) {
						String wd = (lastDir != null) ? lastDir : System.getProperty("user.dir");

						JFileChooser fc = new JFileChooser(wd);
						EditorFileFilter xmlFilter = new EditorFileFilter(".xml", "Fichier XML");
						EditorFileFilter yscFilter = new EditorFileFilter(".ysc", "Fichier Yakindu");

						fc.addChoosableFileFilter(xmlFilter);
						fc.addChoosableFileFilter(yscFilter);

						fc.setAcceptAllFileFilterUsed(false);
						fc.setFileFilter(xmlFilter);
						setFileChooserFont(fc.getComponents());

						int response = fc.showDialog(null, "Ouvrir un automate");

						if (response == JFileChooser.APPROVE_OPTION) {
							lastDir = fc.getSelectedFile().getParent();

							if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".xml")) {
								// Read in the file
								try {
									app.setCurrentFile(fc.getSelectedFile());
									((mxGraphModel) graphComponent.getGraph().getModel()).clear();

									XmlHandler xmlHandler = new XmlHandler(app);
									app.updateGraph(xmlHandler.readXml(fc.getSelectedFile()));
								} catch (Throwable exception) {
									exception.printStackTrace();
									JOptionPane.showMessageDialog(graphComponent, exception.toString(), "Erreur",
											JOptionPane.ERROR_MESSAGE);
								}
							} else if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".ysc")) {
								//
							}
						}
					}
				}
			}
		}

		public void setFileChooserFont(Component[] comps) {
			for (Component comp : comps) {
				if (comp instanceof Container) {
					setFileChooserFont(((Container) comp).getComponents());
				}

				try {
					comp.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
				} catch (Exception e) {
					//
				}
			}
		}
	}

	@SuppressWarnings("serial")
	public static class SaveAction extends AbstractAction {

		protected String lastDir = null;

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				mxGraphComponent graphComponent = (mxGraphComponent) app.getGraphComponent();
				String filename = null;

				// START OF GETTING THE FILE TO HOLD THE GRAPH
				if (app.getCurrentFile() == null) {
					// No file in the app context
					String wd;

					if (lastDir != null) {
						wd = lastDir;
					} else if (app.getCurrentFile() != null) {
						wd = app.getCurrentFile().getParent();
					} else {
						wd = System.getProperty("user.dir");
					}

					JFileChooser fc = new JFileChooser(wd);
					EditorFileFilter xmlFilter = new EditorFileFilter(".xml", "Fichier XML");
					fc.setAcceptAllFileFilterUsed(false);
					fc.setFileFilter(xmlFilter);
					setFileChooserFont(fc.getComponents());
					int response = fc.showDialog(null, "Enregistrer l'automate");

					if (response != JFileChooser.APPROVE_OPTION) {
						return;
					} else {
						lastDir = fc.getSelectedFile().getParent();
					}

					filename = fc.getSelectedFile().getAbsolutePath();

					if (!filename.toLowerCase().endsWith(xmlFilter.getExtension())) {
						filename += xmlFilter.getExtension();
					}

					// Check if named file already exists
					UIManager.put("OptionPane.messageFont", new Font("Ubuntu Mono", Font.PLAIN, 14));
					UIManager.put("OptionPane.buttonFont", new Font("Ubuntu Mono", Font.PLAIN, 14));

					if (new File(filename).exists() && JOptionPane.showConfirmDialog(graphComponent,
							"Fusionner avec le fichier existant ?") != JOptionPane.YES_OPTION) {
						return;
					}
				} else {
					filename = app.getCurrentFile().getAbsolutePath();
				}
				// END GETTING THE FILE

				// Write in the file
				try {
					XmlHandler xmlHandler = new XmlHandler(app);
					mxUtils.writeFile(xmlHandler.getAsXml((mxGraph) app.getGraphComponent().getGraph()), filename);
					app.status("Fichier sauvegardé avec succès");
					app.setCurrentFile(new File(filename));
				} catch (Throwable exception) {
					exception.printStackTrace();
					JOptionPane.showMessageDialog(graphComponent, exception.toString(), "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		public void setFileChooserFont(Component[] comps) {
			for (Component comp : comps) {
				if (comp instanceof Container) {
					setFileChooserFont(((Container) comp).getComponents());
				}

				try {
					comp.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
				} catch (Exception e) {
					//
				}
			}
		}

	}

	@SuppressWarnings("serial")
	public static class CloseAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				app.exit();
			}
		}

	}

	@SuppressWarnings("serial")
	public static class QuitAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				System.out.println("QUIT CLICKED");
			}
		}

	}

	@SuppressWarnings("serial")
	public static class SimulatorAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				System.out.println("SIMULATOR CLICKED");
			}
		}

	}

	@SuppressWarnings("serial")
	public static class AboutAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				System.out.println("ABOUT CLICKED");
			}
		}

	}

	@SuppressWarnings("serial")
	public static class CursorAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				System.out.println("CURSOR CLICKED");
			}
		}

	}

	@SuppressWarnings("serial")
	public static class StateAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				System.out.println("STATE CLICKED");
			}
		}

	}

	@SuppressWarnings("serial")
	public static class EdgeAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				System.out.println("EDGE CLICKED");
			}
		}

	}

	@SuppressWarnings("serial")
	public static class HistoryAction extends AbstractAction {
		protected boolean undo;

		public HistoryAction(boolean undo) {
			this.undo = undo;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			App app = getApp(e);

			if (app != null) {
				if (undo) {
					app.getUndoManager().undo();
				} else {
					app.getUndoManager().redo();
				}
			}
		}

	}
}
