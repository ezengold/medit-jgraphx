package verifier.ast;


import verifier.visitor.Visitor;

public class BooleanLiteral implements Exp {
	private boolean value;
	
	public BooleanLiteral(boolean value) {
		this.value = value;
	}
	
	public boolean getValue() {
		return value;
	}
	
	public void accept(Visitor v) {
		v.visit(this);
	}

	@Override
	public String toString() {
		return value ?"True":"False";
	}
}
