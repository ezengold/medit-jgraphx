package ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;

import org.jgrapht.*;
import org.jgrapht.ext.*;
import org.jgrapht.graph.*;

import com.mxgraph.swing.*;
import com.mxgraph.layout.*;

public class Editor extends JPanel {
	private static final long serialVersionUID = 1L;

	private JGraphXAdapter<String, DefaultEdge> jgxAdapter;

	public Editor(JPanel PARENT) {
		// create a JGraphT graph
		ListenableGraph<String, DefaultEdge> g = new DefaultListenableGraph<>(
				new DefaultDirectedGraph<>(DefaultEdge.class));

		// Create a visualization using JGraph, via an adapter
		jgxAdapter = new JGraphXAdapter<>(g);

		this.setSize(PARENT.getSize());
		this.setPreferredSize(PARENT.getPreferredSize());
		
		mxGraphComponent component = new mxGraphComponent(jgxAdapter);
		component.setConnectable(false);
		component.getGraph().setAllowDanglingEdges(false);
		component.setBackground(Color.WHITE);

		this.setLayout(new BorderLayout());
		this.add(component, BorderLayout.CENTER);

		String v1 = "v1";
		String v2 = "v2";
		String v3 = "v3";
		String v4 = "v4";

		// add some sample data (graph manipulated via JGraphX)
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);

		g.addEdge(v1, v2);
		g.addEdge(v2, v3);
		g.addEdge(v3, v1);
		g.addEdge(v4, v3);

		// positioning via jgraphx layouts
		mxCircleLayout layout = new mxCircleLayout(jgxAdapter);

		// center the circle
		int radius = 100;
		layout.setX0(20);
		layout.setY0(20);
		layout.setRadius(radius);
		layout.setMoveCircle(true);

		layout.execute(jgxAdapter.getDefaultParent());
		// that's all there is to it!...
	}
}
