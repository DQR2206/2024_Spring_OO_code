import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Term {
    private final ArrayList<Factor> factors;
    private int sign;

    public Term(int sign) {
        this.sign = sign;
        this.factors = new ArrayList<>();
    }

    public void addFactor(Factor factor) {
        this.factors.add(factor);
    }

    public Poly toPoly() {
        HashMap<Integer, BigInteger> unitMap = new HashMap<>();
        Poly poly = new Poly(unitMap);
        for (Factor factor : factors) {
            poly = poly.multiPoly(factor.toPoly());
        }
        if (this.sign == -1) {
            poly.negate();
        }
        return poly;
    }
}
