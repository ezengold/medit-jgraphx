package verifier.ast;


import verifier.visitor.Visitor;

public interface Type {
	public void accept(Visitor v);
}
