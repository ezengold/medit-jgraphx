package verifier.ast;

import verifier.visitor.Visitor;

public class CharArrayType implements Type {
	public void accept(Visitor v) {
		v.visit(this);
	}
}
