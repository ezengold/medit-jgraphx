package verifier.ast;

import verifier.visitor.Visitor;

public class True implements Exp {
	public void accept(Visitor v) {
		v.visit(this);
	}
}



