package verifier.ast;


import verifier.visitor.Visitor;

public class ExistsNext implements Exp {

    private  Exp exp;

    public ExistsNext(Exp exp) {
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
        return "E-> "+exp.toString();
    }

}
