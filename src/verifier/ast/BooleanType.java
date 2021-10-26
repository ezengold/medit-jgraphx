package verifier.ast;


import verifier.visitor.Visitor;

public class BooleanType implements Type {
	public void accept(Visitor v) {
		v.visit(this);
	}
}
