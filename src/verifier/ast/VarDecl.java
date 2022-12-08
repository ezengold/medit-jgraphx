package verifier.ast;

import verifier.visitor.Visitor;

public class VarDecl {
	private Type type;
	private Identifier id;
	private Exp value;
	
	public VarDecl(Type type, Identifier id) {
		this.type = type;
		this.id = id;
		this.value = null;
	}
	
	public VarDecl(Type type, Identifier id, Exp value) {
		this.type = type;
		this.id = id;
		this.value = value;
	}
	
	public Type getType() {
		return type;
	}
	
	public Identifier getId() {
		return id;
	}
	
	public Exp getValue() {
		return value;
	}

	public void setValue(Exp value) {
		this.value = value;
	}

	public void accept(Visitor v) {
		v.visit(this);
	}


}
