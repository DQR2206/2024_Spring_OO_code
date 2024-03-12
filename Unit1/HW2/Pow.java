import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;

public class Pow implements Factor {
    private BigInteger pow;

    public Pow(BigInteger pow) {
        this.pow = pow;
    }

    @Override
    public Poly toPoly() {    // x^n
        HashMap<Unit,BigInteger> unitMap = new HashMap<>();
        Unit unit = new Unit(this.pow,new HashMap<>());
        unitMap.put(unit,BigInteger.ONE);
        return new Poly(unitMap);
    }

    @Override
    public String toString() {
        if (Objects.equals(this.pow, BigInteger.ONE)) {
            return "x";
        } else if (Objects.equals(this.pow, BigInteger.ZERO)) {
            return "1";
        } else {
            return "x^" + this.pow;
        }
    }
}
