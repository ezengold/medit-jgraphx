package verifier.ast;

import verifier.visitor.Visitor;

public class ExistsUntil implements  Exp {

    private Exp lhs, rhs;

    public ExistsUntil(Exp lhs, Exp rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Exp getLhs() {
        return lhs;
    }

    public Exp getRhs() {
        return rhs;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public String toString() {
        return "E "+lhs.toString()+" UNTIL "+rhs.toString();
    }
}
