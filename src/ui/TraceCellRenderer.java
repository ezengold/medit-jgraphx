package ui;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import models.Trace;

public class TraceCellRenderer extends Button implements TableCellRenderer {

	private ArrayList<Trace> traces = new ArrayList<Trace>();

	public TraceCellRenderer(ArrayList<Trace> traces) {
		super();
		this.traces = traces;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		setHorizontalAlignment(SwingConstants.LEFT);
		setBackground(isSelected ? Color.decode("#9099ae") : new Color(0, 0, 0, 0));

		// get the trace id forwarded
		String traceId = value.toString();

		if (!traceId.isEmpty()) {
			Trace trace = findTrace(traceId);

			if (trace != null) {
				setText((trace.getCurrentState() != null ? trace.getCurrentState().getName() : "<??>") + " --> "
						+ (trace.getTargetState() != null ? trace.getTargetState().getName() : "<??>"));
			} else {
				// should never occures
				setText("<? not found ?>");
			}

			return this;
		} else {
			return null;
		}
	}

	public Trace findTrace(String traceId) {
		for (Trace trace : traces) {
			if (trace.getTraceId().equals(traceId))
				return trace;
		}
		return null;
	}

	public void setTraces(ArrayList<Trace> traces) {
		this.traces = traces;
	}

	public ArrayList<Trace> getTraces() {
		return traces;
	}
}
