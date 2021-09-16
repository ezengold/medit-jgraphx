package utils;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.util.mxGraphActions;

public class EditorKeyboardHandler extends mxKeyboardHandler {

	public EditorKeyboardHandler(mxGraphComponent graphComponent) {
		super(graphComponent);
	}

	protected InputMap getInputMap(int condition) {
		InputMap map = super.getInputMap(condition);
		
		if (condition == JComponent.WHEN_FOCUSED && map != null) {
			map.put(KeyStroke.getKeyStroke("control S"), "save");
			map.put(KeyStroke.getKeyStroke("control shift S"), "saveAs");
			map.put(KeyStroke.getKeyStroke("control N"), "new");
			map.put(KeyStroke.getKeyStroke("control O"), "open");

			map.put(KeyStroke.getKeyStroke("control Z"), "undo");
			map.put(KeyStroke.getKeyStroke("control Y"), "redo");
			
			map.put(KeyStroke.getKeyStroke("control shift V"), "selectVertices");
			map.put(KeyStroke.getKeyStroke("control shift E"), "selectEdges");
		}
		
		return map;
	}
	
	protected ActionMap createActionMap() {
		ActionMap map = super.createActionMap();
		
		map.put("save", new EditorActions.SaveAction());
		map.put("saveAs", new EditorActions.SaveAction());
		map.put("new", new EditorActions.NewAction());
		map.put("open", new EditorActions.OpenAction());
		map.put("undo", new EditorActions.HistoryAction(true));
		map.put("redo", new EditorActions.HistoryAction(false));
		map.put("selectVertices", mxGraphActions.getSelectVerticesAction());
		map.put("selectEdges", mxGraphActions.getSelectEdgesAction());

		return map;
	}
}
