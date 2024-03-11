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

    public Poly toPoly() {
        HashMap<Integer, BigInteger> unitMap = new HashMap<>();
        Poly poly = new Poly(unitMap);
        for (Term term : terms) {
            poly = poly.addPoly(term.toPoly());
        }
        //最后考虑乘方操作
        poly = poly.powPoly(this.pow);
        return poly;
    }
}
