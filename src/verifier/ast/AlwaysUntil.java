package verifier.ast;

import verifier.visitor.Visitor;

public class AlwaysUntil implements  Exp {

    private Exp lhs, rhs;

    public AlwaysUntil(Exp lhs, Exp rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Exp getRhs() {
        return rhs;
    }

    public Exp getLhs() {
        return lhs;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public String toString() {
        return "A "+lhs.toString()+" UNTIL "+rhs.toString();
    }

}
