import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Number implements Factor {
    private final BigInteger num;

    public Number(BigInteger num) {
        this.num = num;
    }

    //将数字转化为一个只有一项单项式的多项式
    public Poly toPoly() {
        //形式化说明中保证了输入数字因子只会有 n的形式 而不会有 n^n的形式 不用考虑太多
        HashMap<Integer,BigInteger> unitMap = new HashMap<>();
        unitMap.put(0,this.num);
        return new Poly(unitMap);
    }
}
