package utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxCellState;


import app.App;
import app.App.Compilables;
import models.State;
import models.Transition;
import ui.GraphStyles;

public class XmlHandler {
	private App app;

	private static HashMap<String, String> excepts = new HashMap<String, String>();

	public XmlHandler(App parent) {
		this.app = parent;

		excepts.put("<", "&lt;");
		excepts.put(">", "&gt;");
		excepts.put("&", "&amp;");
	}

	/**
	 * Fill the automata model with the states and edges while looping on the graph
	 * 
	 * @param graph
	 * @return String
	 */
	public String getAsXml(final mxGraph graph) {
		// restore the automata arrays of states and transitions
		this.app.getAutomata().setStatesList(new ArrayList<State>());
		this.app.getAutomata().setTransitionsList(new ArrayList<Transition>());
		this.app.getAutomata().setDeclarationsList(new ArrayList<String>());

		String globalDeclarations = escapeStr((this.app.getGlobalDeclarations()));
		String xmlStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + "<!-- DTD GOES HERE -->\n" + "<content>\n";

		// add global declarations
		xmlStr += "\t<declarations>" + globalDeclarations + "</declarations>\n";

		// save declarations in the automata model
		for (String str : globalDeclarations.split(";")) {
			this.app.getAutomata().addDeclaration(str);
		}

		// add model
		xmlStr += "\t<model>\n";
		Object[] cells = graph.getChildCells(graph.getDefaultParent());

		for (Object el : cells) {
			mxCell cell = (mxCell) el;
			if (cell.isVertex()) {
				if (cell.getValue() instanceof State) {
					State state = (State) cell.getValue();
					state.setPosition(cell.getGeometry().getX(), cell.getGeometry().getY());

					xmlStr += "\t\t<location id=\"" + cell.getId() + "\" stateId=\""
							+ (state != null ? state.getStateId() : "") + "\" x=\"" + cell.getGeometry().getX()
							+ "\" y=\"" + cell.getGeometry().getY() + "\" isInitial=\""
							+ (state != null && state.isInitial() ? "1" : "0") + "\">\n";
					xmlStr += "\t\t\t<name>" + (state != null ? escapeStr(state.getName()) : "") + "</name>\n";
					xmlStr += "\t\t\t<invariant>" + (state != null ? escapeStr(state.getInvariant()) : "")
							+ "</invariant>\n";
					xmlStr += "\t\t</location>\n";

					// save the state in the automata model
					this.app.getAutomata().addState(state);
				}
			} else if (cell.isEdge()) {
				Transition transition = (Transition) cell.getValue();

				xmlStr += "\t\t<transition id=\"" + cell.getId() + "\" transitionId=\""
						+ (transition != null ? transition.getTransitionId() : "") + "\" source=\""
						+ cell.getSource().getId() + "\" sourceX=\"" + cell.getGeometry().getSourcePoint().getX()
						+ "\" sourceY=\"" + cell.getGeometry().getSourcePoint().getY() + "\" target=\""
						+ cell.getTarget().getId() + "\" targetX=\"" + cell.getGeometry().getTargetPoint().getX()
						+ "\" targetY=\"" + cell.getGeometry().getTargetPoint().getY() + "\">\n";
				xmlStr += "\t\t\t<guard>" + (transition != null ? escapeStr(transition.getGuard()) : "") + "</guard>\n";
				xmlStr += "\t\t\t<event>" + (transition != null ? escapeStr(transition.getEvent()) : "") + "</event>\n";
				xmlStr += "\t\t\t<updates>" + (transition != null ? escapeStr(transition.getUpdate()) : "")
						+ "</updates>\n";
				xmlStr += "\t\t</transition>\n";
				// save the transition in the automata model
				assert transition != null;
				this.app.addEvent(transition.getEvent());
				this.app.getAutomata().addTransition(transition);
			}
		}

		xmlStr += "\t</model>\n";
//		System.out.println("XML GOT : "+xmlStr);

		return xmlStr + "</content>\n";
	}

	public mxGraph readXml(File file) throws IOException {
		// restore the automata arrays of states and transitions
		this.app.getAutomata().setStatesList(new ArrayList<State>());
		this.app.getAutomata().setTransitionsList(new ArrayList<Transition>());
		this.app.getAutomata().setDeclarationsList(new ArrayList<String>());

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		mxGraph graph = new mxGraph();

		graph.setCellsResizable(false);
		graph.setCellsEditable(false);
		graph.isLabelMovable(true);

		Hashtable<String, Object> edgeStyle = (Hashtable<String, Object>) graph.getStylesheet().getDefaultEdgeStyle();
		edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OPEN);
		edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ENTITY_RELATION);
		edgeStyle.put(mxConstants.STYLE_ROUNDED, "1");
		graph.getStylesheet().setDefaultEdgeStyle(edgeStyle);

		Hashtable<String, Object> vertexStyle = (Hashtable<String, Object>) graph.getStylesheet()
				.getDefaultVertexStyle();
		vertexStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		vertexStyle.put(mxConstants.STYLE_FILLCOLOR, GraphStyles.FILL_COLOR);
		vertexStyle.put(mxConstants.STYLE_STROKECOLOR, GraphStyles.STROKE_COLOR);
		vertexStyle.put(mxConstants.STYLE_FONTCOLOR, GraphStyles.FONT_COLOR);
		graph.getStylesheet().setDefaultVertexStyle(vertexStyle);

		try {
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();

			// GET DECLARATIONS
			String declarations = escapeStr(doc.getElementsByTagName("declarations").item(0).getTextContent());
			this.app.setGlobalDeclarations(declarations);

			// save declarations in the automata model
			for (String str : declarations.split(";")) {
				this.app.getAutomata().addDeclaration(str);
			}

			graph.getModel().beginUpdate();
			graph.setMultigraph(true);
			graph.setAllowLoops(true);

			try {
				graph.removeCells(graph.getChildCells(graph.getDefaultParent()));

				// Hold all the vertices created
				HashMap<String, Object> verticesArray = new HashMap<String, Object>();

				// GET LOCATIONS
				State.NB = 0;
				NodeList locationsList = doc.getElementsByTagName("location");
				for (int i = 0; i < locationsList.getLength(); i++) {
					Node node = locationsList.item(i);

					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element location = (Element) node;
						String id = location.getAttribute("id");
						String stateId = location.getAttribute("stateId");
						boolean isInitial = location.getAttribute("isInitial").equals("1") ? true : false;
						double x = Double.parseDouble(location.getAttribute("x"));
						double y = Double.parseDouble(location.getAttribute("y"));
						String name = restoreStr(location.getElementsByTagName("name").item(0).getTextContent());
						String invariant = restoreStr(
								location.getElementsByTagName("invariant").item(0).getTextContent());


						State s = new State(name);
						s.setStateId(stateId);
						s.setInitial(isInitial);
						s.setInvariant(invariant);
						s.setPosition(x, y);
						System.out.println("ELEMENT "+s.debug());

						Object vertex = graph.insertVertex(graph.getDefaultParent(), id, s, x, y, 60, 60);
						((mxCell)vertex).setStyle("fontSize= 14");
//						// Get the mxCellState for the cell and set the font size using the setStyle() method
//						mxCellState state = graph.getView().getState(((mxCell)vertex));
//						System.out.println("STATE = "+state);
//						HashMap<String, Object> style = new HashMap<>();
//						style.put("fontSize", 16);
//						state.setStyle(style);







						if (isInitial) {
							((mxCell) vertex).setStyle("fillColor=" + GraphStyles.INIT_FILL_COLOR + ";strokeColor="
									+ GraphStyles.INIT_STROKE_COLOR + ";fontColor=" + GraphStyles.INIT_FONT_COLOR + ";fontSize= 12" );
						}

						verticesArray.put(id, vertex);

						// save the state in the automata model
						this.app.getAutomata().addState(s);
					}
				}

				// GET EDGES
				NodeList edgesList = doc.getElementsByTagName("transition");
				for (int i = 0; i < edgesList.getLength(); i++) {
					Node node = edgesList.item(i);

					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element edge = (Element) node;

						String id = edge.getAttribute("id");
						String transitionId = edge.getAttribute("transitionId");

						String guard = restoreStr(edge.getElementsByTagName("guard").item(0).getTextContent());
						String updates = restoreStr(edge.getElementsByTagName("updates").item(0).getTextContent());
						String event = restoreStr(edge.getElementsByTagName("event").item(0).getTextContent());

						String sourceId = edge.getAttribute("source");
						mxCell source = (mxCell) verticesArray.get(sourceId);
						double sourceX = Double.parseDouble(edge.getAttribute("sourceX"));
						double sourceY = Double.parseDouble(edge.getAttribute("sourceY"));


						String targetId = edge.getAttribute("target");
						mxCell target = (mxCell) verticesArray.get(targetId);
						double targetX = Double.parseDouble(edge.getAttribute("targetX"));
						double targetY = Double.parseDouble(edge.getAttribute("targetY"));

						Transition transition = new Transition();
						transition.setTransitionId(transitionId);


						if (source != null && source.getValue() != null) {
							transition.setSourceStateId(((State) source.getValue()).getStateId());
						}

						if (target != null && target.getValue() != null) {
							transition.setTargetStateId(((State) target.getValue()).getStateId());
						}

						transition.setGuard(guard);
						transition.setUpdate(updates);

						//handling of events
						transition.setEvent(event);
						this.app.addEvent(event);

						mxCell newEdge = (mxCell) graph.insertEdge(graph.getDefaultParent(), id, transition, source,
								target);


						mxGeometry edgeGeometry = new mxGeometry();
						edgeGeometry.setTerminalPoint(new mxPoint(sourceX, sourceY), true);
						edgeGeometry.setTerminalPoint(new mxPoint(targetX, targetY), false);


						edgeGeometry.setRelative(true);
						newEdge.setGeometry(edgeGeometry);
						newEdge.setStyle("fontSize = 20;");


						// save the transition in the automata model
						this.app.getAutomata().addTransition(transition);

					}
				}
			} finally {
				graph.getModel().endUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return graph;
	}

	/*
	 * Read the currentFile and get all compilable blocks
	 */
	public static HashMap<Compilables, ArrayList<String>> getCompilables(File file) throws IOException {
		HashMap<Compilables, ArrayList<String>> output = new HashMap<Compilables, ArrayList<String>>();

		ArrayList<String> decs = new ArrayList<String>();
		ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> conds = new ArrayList<String>();
		ArrayList<String> updts = new ArrayList<String>();
		ArrayList<String> invs = new ArrayList<String>();
		ArrayList<String> events = new ArrayList<>();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();

			// GET DECLARATIONS
			String declarations = doc.getElementsByTagName("declarations").item(0).getTextContent();
			if (!declarations.isEmpty())
				decs.add(declarations);

			// GET STATES DATA
			NodeList locationsList = doc.getElementsByTagName("location");
			for (int i = 0; i < locationsList.getLength(); i++) {
				Node node = locationsList.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element location = (Element) node;

					String name = restoreStr(location.getElementsByTagName("name").item(0).getTextContent());
					String invariant = restoreStr(location.getElementsByTagName("invariant").item(0).getTextContent());

					if (!name.isEmpty())
						ids.add(name);

					if (!invariant.isEmpty())
						invs.add(invariant);
				}
			}

			// GET EDGES DATA
			NodeList edgesList = doc.getElementsByTagName("transition");
			for (int i = 0; i < edgesList.getLength(); i++) {
				Node node = edgesList.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element edge = (Element) node;

					String updates = restoreStr(edge.getElementsByTagName("updates").item(0).getTextContent());
					String guard = restoreStr(edge.getElementsByTagName("guard").item(0).getTextContent());
					String event = restoreStr(edge.getElementsByTagName("event").item(0).getTextContent());

					conds.add((guard.isEmpty() ? "true" : guard) + "^" + (updates.isEmpty() ? "" : updates));

					if (!updates.isEmpty())
						updts.add(updates);
					if(event != null && !event.isEmpty()) {
						events.add(event);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		output.put(Compilables.DECLARATIONS, decs);
		output.put(Compilables.CONDITIONS, conds);
		output.put(Compilables.IDENTIFIERS, ids);
		output.put(Compilables.UPDATES, updts);
		output.put(Compilables.INVARIANTS, invs);

		return output;
	}

	public static String escapeStr(final String input) {
		String output = input;
		for (String token : excepts.keySet()) {
			output = output.replaceAll(token, excepts.get(token));
		}
		return output;
	}

	public static String restoreStr(final String input) {
		String output = input;
		for (String token : excepts.keySet()) {
			output = output.replaceAll(excepts.get(token), token);
		}
		return output;
	}
}
