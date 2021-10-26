package verifier.ast;


import verifier.visitor.Visitor;

public interface Statement {
	public void accept(Visitor v);
}
