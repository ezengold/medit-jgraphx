package verifier.ast;

import verifier.visitor.Visitor;

public class False implements Exp {
	public void accept(Visitor v) {
		v.visit(this);
	}
}
