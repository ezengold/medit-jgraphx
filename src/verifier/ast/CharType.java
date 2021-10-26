package verifier.ast;

import verifier.visitor.Visitor;

public class CharType implements Type {
	public void accept(Visitor v) {
		v.visit(this);
	}
}
