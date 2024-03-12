import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Term implements Factor {
    private final ArrayList<Factor> factors;
    private int sign;

    public Term(int sign) {
        this.sign = sign;
        this.factors = new ArrayList<>();
    }

    public void addFactor(Factor factor) {
        this.factors.add(factor);
    }

    @Override
    public Poly toPoly() {
        HashMap<Unit, BigInteger> unitMap = new HashMap<>();
        Poly poly = new Poly(unitMap);
        for (Factor factor : factors) {
            poly = poly.multiPoly(factor.toPoly());
        }
        if (this.sign == -1) {
            poly.negate();
        }
        return poly;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.sign == -1) {
            sb.append('-');
        }
        for (Factor factor : factors) {
            sb.append(factor.toString());
            sb.append('*');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

}
