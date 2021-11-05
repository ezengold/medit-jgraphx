package verifier.ast;


import verifier.visitor.Visitor;

public class AlwaysNext implements  Exp {
    private  Exp exp;

    public AlwaysNext(Exp exp) {
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
        return "A-> "+exp.toString();
    }
}
