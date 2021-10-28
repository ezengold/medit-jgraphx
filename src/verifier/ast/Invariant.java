package verifier.ast;

import verifier.visitor.Visitor;

public class Invariant implements Statement {
	private Exp value;

	public Invariant(Exp value) {
		this.value = value;
	}

	public Exp getValue() {
		return value;
	}

	public void accept(Visitor v) {
		v.visit(this);
	}

}
