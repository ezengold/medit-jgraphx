package verifier.ast;

import verifier.visitor.Visitor;

public class ClockLiteral implements Exp {
	private long value;

	public ClockLiteral(long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}

}
