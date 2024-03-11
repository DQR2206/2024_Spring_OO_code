import java.math.BigInteger;
import java.util.HashMap;

public class Pow implements Factor {
    private int pow;

    public Pow(int pow) {
        this.pow = pow;
    }

    public Poly toPoly() {
        HashMap<Integer,BigInteger> unitMap =new HashMap<>();
        unitMap.put(pow,BigInteger.ONE);
        return new Poly(unitMap);
    }
}
