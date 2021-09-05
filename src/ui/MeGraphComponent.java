package ui;

import java.awt.Color;
import java.awt.Point;

import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class MeGraphComponent extends mxGraphComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MeGraphComponent(mxGraph graph) {
		super(graph);
	
		setPageVisible(true);
		setGridVisible(true);
		setToolTips(true);
		getConnectionHandler().setCreateTarget(true);
		
		// LOAD EXTERNAL STYLESHEET
		
		getViewport().setOpaque(true);
		getViewport().setBackground(Color.WHITE);
	}
	
	/**
	 * Overrides drop behaviour to set the cell style if the target
	 * is not a valid drop target and the cells are of the same
	 * type (eg. both vertices or both edges). 
	 */
	public Object[] importCells(Object[] cells, double dx, double dy,
			Object target, Point location)
	{
		if (target == null && cells.length == 1 && location != null)
		{
			target = getCellAt(location.x, location.y);

			if (target instanceof mxICell && cells[0] instanceof mxICell)
			{
				mxICell targetCell = (mxICell) target;
				mxICell dropCell = (mxICell) cells[0];

				if (targetCell.isVertex() == dropCell.isVertex()
						|| targetCell.isEdge() == dropCell.isEdge())
				{
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
