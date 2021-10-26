package verifier.ast;

import verifier.visitor.Visitor;

public class FloatArrayType implements Type{
	public void accept(Visitor v) {
		v.visit(this);
	}
}
