package verifier.ast;


import verifier.visitor.Visitor;

public class AlwaysGlobally implements  Exp {
    private  Exp exp;

    public AlwaysGlobally(Exp exp) {
        this.exp = exp;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public String toString() {
        return "A[] "+exp.toString();
    }
}
