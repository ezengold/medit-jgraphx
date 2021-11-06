package ui;

import java.awt.Color;
import java.awt.Point;
import java.util.Hashtable;


import org.jgrapht.ext.JGraphXAdapter;

import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import models.State;
import models.Transition;

public class GraphComponent extends mxGraphComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2637031447186110868L;

	public GraphComponent(JGraphXAdapter<State, Transition> adapter) {
		super(adapter);

		setPageVisible(false);
		setGridVisible(false);
		setToolTips(true);
		getConnectionHandler().setCreateTarget(true);

		final mxGraph graph = getGraph();

		// APPLY STYLES TO GRAPH

		Hashtable<String, Object> edgeStyle = (Hashtable<String, Object>) graph.getStylesheet().getDefaultEdgeStyle();
		edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OPEN);
		edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ENTITY_RELATION);
		// edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_LOOP);
		edgeStyle.put(mxConstants.STYLE_ROUNDED, "1");
		graph.getStylesheet().setDefaultEdgeStyle(edgeStyle);

		graph.setCellsResizable(false);
		graph.setCellsEditable(false);
		graph.isLabelMovable(true);

		Hashtable<String, Object> vertexStyle = (Hashtable<String, Object>) graph.getStylesheet()
				.getDefaultVertexStyle();
		vertexStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		vertexStyle.put(mxConstants.STYLE_FILLCOLOR, "#78c4fc");
		graph.getStylesheet().setDefaultVertexStyle(vertexStyle);

		getViewport().setOpaque(true);
		getViewport().setBackground(Color.WHITE);
	}

	public GraphComponent(mxGraph graphData) {
		super(graphData);

		setPageVisible(false);
		setGridVisible(false);
		setToolTips(true);
		getConnectionHandler().setCreateTarget(true);

		final mxGraph graph = getGraph();

		// APPLY STYLES TO GRAPH

		Hashtable<String, Object> edgeStyle = (Hashtable<String, Object>) graph.getStylesheet().getDefaultEdgeStyle();
		edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OPEN);
		edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ENTITY_RELATION);
		// edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_LOOP);
		edgeStyle.put(mxConstants.STYLE_ROUNDED, "1");
		graph.getStylesheet().setDefaultEdgeStyle(edgeStyle);

		graph.setCellsResizable(false);
		graph.setCellsEditable(false);
		graph.isLabelMovable(true);

		Hashtable<String, Object> vertexStyle = (Hashtable<String, Object>) graph.getStylesheet()
				.getDefaultVertexStyle();
		vertexStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		vertexStyle.put(mxConstants.STYLE_FILLCOLOR, "#78c4fc");
		graph.getStylesheet().setDefaultVertexStyle(vertexStyle);

		getViewport().setOpaque(true);
		getViewport().setBackground(Color.WHITE);
	}

	public Object[] importCells(Object[] cells, double dx, double dy, Object target, Point location) {
		if (target == null && cells.length == 1 && location != null) {
			target = getCellAt(location.x, location.y);

			if (target instanceof mxICell && cells[0] instanceof mxICell) {
				mxICell targetCell = (mxICell) target;
				mxICell dropCell = (mxICell) cells[0];

				if (targetCell.isVertex() == dropCell.isVertex() || targetCell.isEdge() == dropCell.isEdge()) {
					mxIGraphModel model = graph.getModel();
					model.setStyle(target, model.getStyle(cells[0]));
					graph.setSelectionCell(target);

					return null;
				}
			}
		}

		return super.importCells(cells, dx, dy, target, location);
	}

}
