public class Derivation implements Factor {
    private Expr expr;

    public Derivation(Expr expr) {
        this.expr = expr;
    }

    @Override
    public Poly toPoly() {
        Poly poly = expr.toPoly();
        return poly.derive();
    }

    @Override
    public String toString() {
        return '(' + expr.toPoly().derive().toString() + ')';
    }

    @Override
    public Derivation clone() {
        Expr newExpr = (Expr) expr.clone();
        return new Derivation(newExpr);
    }

}
