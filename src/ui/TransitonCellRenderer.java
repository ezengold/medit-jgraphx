package ui;

import java.awt.*;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import models.Automata;
import models.State;
import models.Transition;

public class TransitonCellRenderer extends Button implements TableCellRenderer {

	private Automata automata;

	public TransitonCellRenderer(Automata automata) {
		super();
		this.automata = automata;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		setHorizontalAlignment(SwingConstants.LEFT);
		setBackground(isSelected ? Color.decode("#9099ae") : Color.decode("#dddddd"));

		// get transition id forwarded
		String transitionId = value.toString();

		if (!transitionId.isEmpty()) {
			Transition transition = automata.findTransition(transitionId);

			if (transition != null) {
				State source = automata.findState(transition.getSourceStateId());
				State target = automata.findState(transition.getTargetStateId());

				setText("take : " + (source != null ? source.getName() : transition.getSourceStateId().substring(0, 6))
						+ " --> "
						+ (target != null ? target.getName() : transition.getTargetStateId().substring(0, 6)));
			} else {
				// should never occures
				setText("<? not found ?>");
			}

			return this;
		} else {
			return null;
		}
	}

	public Automata getAutomata() {
		return automata;
	}

	public void setAutomata(Automata automata) {
		this.automata = automata;
	}
}
