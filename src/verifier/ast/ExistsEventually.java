package verifier.ast;


import verifier.visitor.Visitor;

public class ExistsEventually implements  Exp {

    private  Exp exp;

    public ExistsEventually(Exp exp) {
        this.exp = exp;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }


}
