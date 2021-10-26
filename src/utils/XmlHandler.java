package utils;

import java.io.File;
import java.io.IOException;
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

import app.App;
import models.State;
import models.Transition;

public class XmlHandler {
	private App app;

	private HashMap<String, String> excepts = new HashMap<String, String>();

	public XmlHandler(App parent) {
		this.app = parent;

		excepts.put("<", "&gt;");
		excepts.put(">", "&lt;");
		excepts.put("&", "&amp;");
	}

	public String getAsXml(final mxGraph graph) {
		String globalDeclarations = escapeStr((this.app.getGlobalDeclarations()));
		String xmlStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + "<!-- DTD GOES HERE -->\n" + "<content>\n";

		// START Add global declarations
		xmlStr += "\t<declarations>" + globalDeclarations + "</declarations>\n";
		// END Add global declarations

		// START Add model
		xmlStr += "\t<model>\n";
		Object[] cells = graph.getChildCells(graph.getDefaultParent());

		for (Object el : cells) {
			mxCell cell = (mxCell) el;
			if (cell.isVertex()) {
				State state = (State) cell.getValue();

				xmlStr += "\t\t<location id=\"" + cell.getId() + "\" stateId=\""
						+ (state != null ? state.getStateId() : "") + "\" x=\"" + cell.getGeometry().getX() + "\" y=\""
						+ cell.getGeometry().getY() + "\" isInitial=\""
						+ (state != null && state.isInitial() ? "1" : "0") + "\">\n";
				xmlStr += "\t\t\t<name>" + (state != null ? escapeStr(state.getName()) : "") + "</name>\n";
				xmlStr += "\t\t\t<invariant>" + (state != null ? escapeStr(state.getInvariant()) : "")
						+ "</invariant>\n";
				xmlStr += "\t\t</location>\n";
			} else if (cell.isEdge()) {
				Transition transition = (Transition) cell.getValue();

				xmlStr += "\t\t<transition id=\"" + cell.getId() + "\" transitionId=\""
						+ (transition != null ? transition.getTransitionId() : "") + "\" source=\""
						+ cell.getSource().getId() + "\" sourceX=\"" + cell.getGeometry().getSourcePoint().getX()
						+ "\" sourceY=\"" + cell.getGeometry().getSourcePoint().getY() + "\" target=\""
						+ cell.getTarget().getId() + "\" targetX=\"" + cell.getGeometry().getTargetPoint().getX()
						+ "\" targetY=\"" + cell.getGeometry().getTargetPoint().getY() + "\">\n";
				xmlStr += "\t\t\t<guard>" + (transition != null ? escapeStr(transition.getGuard()) : "")
						+ "</guard>\n";
				xmlStr += "\t\t\t<updates>" + (transition != null ? escapeStr(transition.getUpdate()) : "")
						+ "</updates>\n";
				xmlStr += "\t\t</transition>\n";
			}
		}

		xmlStr += "\t</model>\n";
		// END Add model

		return xmlStr + "</content>\n";
	}

	public mxGraph readXml(File file) throws IOException {
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
		vertexStyle.put(mxConstants.STYLE_FILLCOLOR, "#78c4fc");
		graph.getStylesheet().setDefaultVertexStyle(vertexStyle);

		try {
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();

			// GET DECLARATIONS
			String declarations = doc.getElementsByTagName("declarations").item(0).getTextContent();
			this.app.setGlobalDeclarations(declarations);

			graph.getModel().beginUpdate();
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

						Object vertex = graph.insertVertex(graph.getDefaultParent(), id, s, x, y, 20, 20);

						if (isInitial) {
							((mxCell) vertex).setStyle("fillColor=#888888;strokeColor=#dddddd");
						}

						verticesArray.put(id, vertex);
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

						mxCell newEdge = (mxCell) graph.insertEdge(graph.getDefaultParent(), id, transition, source,
								target);

						mxGeometry edgeGeometry = new mxGeometry();
						edgeGeometry.setTerminalPoint(new mxPoint(sourceX, sourceY), true);
						edgeGeometry.setTerminalPoint(new mxPoint(targetX, targetY), false);
						edgeGeometry.setRelative(true);
						newEdge.setGeometry(edgeGeometry);
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

	public String escapeStr(final String input) {
		String output = input;
		for (String token : excepts.keySet()) {
			output = output.replaceAll(token, excepts.get(token));
		}
		return output;
	}

	public String restoreStr(final String input) {
		String output = input;
		for (String token : excepts.keySet()) {
			output = output.replaceAll(excepts.get(token), token);
		}
		return output;
	}
}
