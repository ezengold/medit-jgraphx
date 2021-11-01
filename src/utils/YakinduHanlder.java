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

import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import app.App;
import models.State;
import models.Transition;

public class YakinduHanlder {
	private App app;

	protected String globalDeclations = "";
	protected ArrayList<State> stateList = new ArrayList<State>();
	protected ArrayList<Transition> transitionList = new ArrayList<Transition>();
	protected HashMap<String, String> transitionUpdates = new HashMap<String, String>();
	protected HashMap<String, String> transitionGuards = new HashMap<String, String>();

	private static HashMap<String, String> excepts = new HashMap<String, String>();

	private static HashMap<String, String> globalDeclarationsExcepts = new HashMap<String, String>();

	public YakinduHanlder(App parent) {
		this.app = parent;

		excepts.put("[", "");
		excepts.put("]", "");
		excepts.put("_", ".");
		excepts.put("always", "");

		excepts.put("&#xA;", "\n");
		excepts.put("&#xD;", "\n");
		excepts.put("&#x9;", "\n");
	}

	public mxGraph readYakinduFile(File file) throws IOException {
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

		// get all states and edges
		try {
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();

			// handle global declarations
			Element decsElement = (Element) doc.getElementsByTagName("sgraph:Statechart").item(0);

			if (decsElement != null) {
				this.globalDeclations = escapeGlabalStr(decsElement.getAttribute("specification"));
			}

			Element firstRegionOfLocations = (Element) doc.getElementsByTagName("regions").item(0);

			if (firstRegionOfLocations != null) {
				NodeList locationsList = firstRegionOfLocations.getElementsByTagName("vertices");

				for (int i = 0; i < locationsList.getLength(); i++) {
					Node node = locationsList.item(i);

					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element location = (Element) node;

						boolean isInitial = location.getAttribute("xsi:type").equals("sgraph:Entry") ? true : false;
						String id = location.getAttribute("xmi:id").trim();

						if (isInitial) {
							State s = new State();
							s.setStateId(id);
							s.setInitial(true);

							stateList.add(s);

							// try getting outgoing transitions
							NodeList outgoingTransitions = location.getElementsByTagName("outgoingTransitions");

							for (int j = 0; j < outgoingTransitions.getLength(); j++) {
								Node transitionNode = outgoingTransitions.item(j);

								if (transitionNode.getNodeType() == Node.ELEMENT_NODE) {
									Element transition = (Element) transitionNode;

									String transitionId = transition.getAttribute("xmi:id").trim();

									// create the transition
									Transition tr = new Transition(id, transition.getAttribute("target"));

									// there is no guard and update on the entry state, we add it to the array
									transitionList.add(tr);
								}
							}
						} else {
							String name = location.getAttribute("name");

							State s = new State(name);
							s.setStateId(id);
							s.setInitial(false);

							stateList.add(s);

							// try getting outgoing transitions
							NodeList outgoingTransitions = location.getElementsByTagName("outgoingTransitions");

							// check specifications on state
							String specs = location.getAttribute("specification");

							if (specs != null) {
								String[] elmts = specs.split("/");

								if (elmts[0] != null && elmts[0].equals("entry")) {
									// get incoming transitions IDs on the vertices
									String[] ids = location.getAttribute("incomingTransitions").split(" ");

									for (String incomingTransitionId : ids) {
										setTransitionUpdate(incomingTransitionId, elmts[1]);
									}

								} else if (elmts[0] != null && elmts[0].equals("exits")) {
									for (int j = 0; j < outgoingTransitions.getLength(); j++) {
										Node transitionNode = outgoingTransitions.item(j);

										if (transitionNode.getNodeType() == Node.ELEMENT_NODE) {
											Element transition = (Element) transitionNode;

											String outgoingTransitionId = transition.getAttribute("xmi:id").trim();
											setTransitionUpdate(outgoingTransitionId, elmts[1]);
										}
									}
								}
							}

							for (int j = 0; j < outgoingTransitions.getLength(); j++) {
								Node transitionNode = outgoingTransitions.item(j);

								if (transitionNode.getNodeType() == Node.ELEMENT_NODE) {
									Element transition = (Element) transitionNode;

									String transitionId = transition.getAttribute("xmi:id").trim();

									// create the transition
									Transition tr = new Transition(id, transition.getAttribute("target"));

									// there is no guard and update on the entry state, we add it to the array
									transitionList.add(tr);

									// handle specifications on the edges
									String transitionSpecification = transition.getAttribute("specification").trim();

									if (!transitionSpecification.isEmpty()) {

										String[] elmts = specs.split("/");

										if (elmts.length > 0)
											setTransitionGuard(transitionId, escapeStr(elmts[0]));

										if (elmts.length > 1)
											setTransitionUpdate(transitionId, escapeStr(elmts[1]));
									}
								}
							}
						}

					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return graph;
	}

	public void setTransitionUpdate(String transitionId, String update) {
		if (this.transitionUpdates.containsKey(transitionId)) {

			String oldValue = this.transitionUpdates.get(transitionId);
			this.transitionUpdates.replace(transitionId, oldValue + ";" + update);

		} else {

			this.transitionUpdates.put(transitionId, update);

		}
	}

	public void setTransitionGuard(String transitionId, String guard) {
		if (this.transitionGuards.containsKey(transitionId)) {

			String oldValue = this.transitionGuards.get(transitionId);
			this.transitionGuards.replace(transitionId, oldValue + ";" + guard);

		} else {

			this.transitionGuards.put(transitionId, guard);

		}
	}

	public static String escapeStr(final String input) {
		String output = input;
		for (String token : excepts.keySet()) {
			output = output.replaceAll(token, excepts.get(token));
		}
		return output;
	}

	public static String escapeGlabalStr(final String input) {
		String output = input;
		for (String token : globalDeclarationsExcepts.keySet()) {
			output = output.replaceAll(token, globalDeclarationsExcepts.get(token));
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