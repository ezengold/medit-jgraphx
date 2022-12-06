package verifier.ast;

import verifier.visitor.Visitor;

public class ChanType implements Type {

    public void accept(Visitor v) {
        v.visit(this);
    }
}
