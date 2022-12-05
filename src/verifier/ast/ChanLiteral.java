package verifier.ast;

import verifier.visitor.Visitor;

public class ChanLiteral implements Exp {
    private String value;

    public ChanLiteral(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
