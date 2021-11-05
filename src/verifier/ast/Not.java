package verifier.ast;

import verifier.visitor.Visitor;

public class Not implements Exp {
	private Exp exp;
	
	public Not(Exp exp) {
		this.exp = exp;
	}
	
	public Exp getExp() {
		return exp;
	}

	@Override
	public String toString() {
		return "Not "+exp.toString();
	}

	public void accept(Visitor v) {
		v.visit(this);
	}
}
