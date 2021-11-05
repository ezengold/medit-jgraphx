package verifier.ast;

import verifier.visitor.Visitor;

public interface Exp {
	public void accept(Visitor v);

}
