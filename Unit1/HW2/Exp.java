import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;

public class Exp implements Factor {
    private Factor factor;
    private BigInteger pow;

    public Exp(Factor factor, BigInteger pow) {
        this.factor = factor;
        this.pow = pow;
    }

    @Override
    public String toString() { // exp(factor)^n toString
        if (Objects.equals(pow, BigInteger.ONE)) {
            return "exp(" + factor.toString() + ")";
        } else {
            return "exp(" + factor.toString() + ")^" + pow;
        }
    }

    @Override
    public Poly toPoly() {
        // ax^b exp(<factor>) ^ n
        HashMap<Unit, BigInteger> unitMap = new HashMap<>();
        HashMap<Poly, BigInteger> expMap = new HashMap<>();
        expMap.put(this.factor.toPoly(), this.pow);
        Unit unit = new Unit(BigInteger.ZERO,expMap);
        unitMap.put(unit, BigInteger.ONE);
        return new Poly(unitMap);
    }

}
