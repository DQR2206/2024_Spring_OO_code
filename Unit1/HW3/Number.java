import java.math.BigInteger;
import java.util.HashMap;

public class Number implements Factor {
    private final BigInteger num;

    public Number(BigInteger num) {
        this.num = num;
    }

    //将数字转化为一个只有一项单项式的多项式
    @Override
    public Poly toPoly() {
        // 转化为只有一项的多项式 ax^b exp(<factor>) ^ n
        HashMap<Unit,BigInteger> unitMap = new HashMap<>();
        //不需要指数形式，暂时把expMap设为空
        Unit unit = new Unit(BigInteger.ZERO,new HashMap<>()); // 数字的hash为空
        unitMap.put(unit,this.num); // 系数设置为数字值
        return new Poly(unitMap);
    }

    @Override
    public String toString() {
        return num.toString();
    }

    @Override
    public Factor clone() {
        return new Number(this.num);
    }
}
