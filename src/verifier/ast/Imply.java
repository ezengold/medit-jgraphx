package verifier.ast;


import verifier.visitor.Visitor;

public class Imply implements Exp {
    private Exp lhs, rhs;

    public Imply(Exp lhs, Exp rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Exp getLHS() {
        return lhs;
    }

    public Exp getRHS() {
        return rhs;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public String toString() {
        return lhs.toString()+" Imply "+rhs.toString();
    }
}
