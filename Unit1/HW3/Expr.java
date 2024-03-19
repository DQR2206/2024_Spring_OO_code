import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Expr implements Factor {
    private final ArrayList<Term> terms;
    private int pow;

    public Expr() {
        this.terms = new ArrayList<>();
        this.pow = 1; //默认为1
    }

    public void setPow(int pow) {
        this.pow = pow;
    }

    public void addTerm(Term term) {
        this.terms.add(term);
    }

    @Override
    public Poly toPoly() {
        HashMap<Unit, BigInteger> unitMap = new HashMap<>();
        Poly poly = new Poly(unitMap);
        for (Term term : terms) {
            poly = poly.addPoly(term.toPoly());
        }
        //最后考虑乘方操作
        poly = poly.powPoly(this.pow);
        return poly;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Term term : terms) {
            sb.append(term.toString());
            sb.append('+');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(')');
        if (this.pow != 1) {
            sb.append('^');
            sb.append(this.pow);
        }
        return sb.toString();
    }

    @Override
    public Factor clone() {
        Expr expr = new Expr();
        for (int i = 0; i < terms.size(); i++) {
            expr.addTerm((Term) terms.get(i).clone());
        }
        expr.pow = this.pow;
        return expr;
    }
}
