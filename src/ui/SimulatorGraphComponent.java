package ui;

import java.awt.*;
import java.util.Hashtable;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

public class SimulatorGraphComponent extends mxGraphComponent {
	public SimulatorGraphComponent(mxGraph graphData) {
		super(graphData);

		setPageVisible(false);
		setGridVisible(false);
		setToolTips(true);
		setEnabled(false);
		setBorder(null);
		getConnectionHandler().setCreateTarget(true);

		final mxGraph graph = getGraph();

		Hashtable<String, Object> edgeStyle = (Hashtable<String, Object>) graph.getStylesheet().getDefaultEdgeStyle();
		edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OPEN);
		edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ENTITY_RELATION);
		edgeStyle.put(mxConstants.STYLE_ROUNDED, "1");
		graph.getStylesheet().setDefaultEdgeStyle(edgeStyle);

		graph.setCellsResizable(false);
		graph.setCellsEditable(false);
		graph.isLabelMovable(false);

		graph.setMultigraph(false);
		graph.setAllowDanglingEdges(false);
		graph.setDisconnectOnMove(false);
		graph.setVertexLabelsMovable(false);

		graph.setResetViewOnRootChange(false);

		Hashtable<String, Object> vertexStyle = (Hashtable<String, Object>) graph.getStylesheet()
				.getDefaultVertexStyle();
		vertexStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		vertexStyle.put(mxConstants.STYLE_FILLCOLOR, "#78c4fc");
		graph.getStylesheet().setDefaultVertexStyle(vertexStyle);

		getViewport().setOpaque(true);
		getViewport().setBackground(Color.WHITE);
	}

	@Override
	public void setGraph(mxGraph newGraph) {
		Hashtable<String, Object> edgeStyle = (Hashtable<String, Object>) newGraph.getStylesheet()
				.getDefaultEdgeStyle();
		edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OPEN);
		edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ENTITY_RELATION);
		edgeStyle.put(mxConstants.STYLE_ROUNDED, "1");
		newGraph.getStylesheet().setDefaultEdgeStyle(edgeStyle);

		newGraph.setCellsResizable(false);
		newGraph.setCellsEditable(false);
		newGraph.isLabelMovable(false);

		newGraph.setMultigraph(false);
		newGraph.setAllowDanglingEdges(false);
		newGraph.setDisconnectOnMove(false);
		newGraph.setVertexLabelsMovable(false);

		newGraph.setResetViewOnRootChange(false);

		Hashtable<String, Object> vertexStyle = (Hashtable<String, Object>) newGraph.getStylesheet()
				.getDefaultVertexStyle();
		vertexStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		vertexStyle.put(mxConstants.STYLE_FILLCOLOR, "#78c4fc");
		newGraph.getStylesheet().setDefaultVertexStyle(vertexStyle);

		super.setGraph(newGraph);
	}
}
