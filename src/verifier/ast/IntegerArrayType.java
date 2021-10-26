package verifier.ast;

import verifier.visitor.Visitor;

public class IntegerArrayType implements Type {
	public void accept(Visitor v) {
		v.visit(this);
	}
}
