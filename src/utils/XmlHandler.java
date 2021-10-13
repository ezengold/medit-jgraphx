package utils;

import java.io.File;
import java.util.HashMap;

import com.mxgraph.model.mxCell;
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

	public String getAsXml() {
		mxGraph graph = this.app.getGraphComponent().getGraph();
		String globalDeclarations = escapeStr((this.app.getGlobalDeclarations()));
		String xmlStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + "<!-- DTD GOES HERE -->\n" + "<content>\n";

		// START Add global declarations
		xmlStr += "\t<declarations>" + globalDeclarations + "\n\t</declarations>\n";
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
				xmlStr += "\t\t\t<guard>" + (transition != null ? escapeStr(transition.getGuardInstructions()) : "")
						+ "</guard>\n";
				xmlStr += "\t\t\t<updates>" + (transition != null ? escapeStr(transition.getUpdateInstructions()) : "")
						+ "</updates>\n";
				xmlStr += "\t\t</transition>\n";
			}
		}

		xmlStr += "\t</model>\n";
		// END Add model

		return xmlStr + "</content>\n";
	}

	public void readXml(File file) {
		//
	}

	public String escapeStr(final String input) {
		String output = input;
		for (String token : excepts.keySet()) {
			output = output.replaceAll(token, excepts.get(token));
		}
		System.out.println("input : " + input + " | output : " + output);
		return output;
	}

	public String restoreStr(final String input) {
		String output = input;
		for (String token : excepts.keySet()) {
			output.replaceAll(excepts.get(token), token);
		}
		return output;
	}

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}
}
