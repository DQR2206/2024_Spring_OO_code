import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class Poly {
    private HashMap<Integer,BigInteger> unitmap;

    public Poly(HashMap<Integer,BigInteger> unitmap) {
        this.unitmap = unitmap;
    }

    //多项式运算方法
    public Poly addPoly(Poly another) {
        Iterator<Map.Entry<Integer, BigInteger>> iterator = this.unitmap.entrySet().iterator();
        while (((Iterator<?>) iterator).hasNext()) {
            Map.Entry<Integer,BigInteger> entry = iterator.next();
            int pow = entry.getKey();
            BigInteger coe = entry.getValue();
            if (another.unitmap.containsKey(pow)) {
                BigInteger coe1 = another.unitmap.get(pow).add(coe);
                another.unitmap.put(pow,coe1);
            } else {
                another.unitmap.put(pow,coe);
            }
            iterator.remove();
        }
        return another;
    }

    public Poly multiPoly(Poly another) {
        if (this.unitmap.isEmpty()) {
            return new Poly(another.unitmap);
        } else {
            HashMap<Integer, BigInteger> emptyMap = new HashMap<>();
            Poly emptypoly = new Poly(emptyMap);
            HashMap<Integer, BigInteger> hashMap = new HashMap<>();
            for (int pow1 : this.unitmap.keySet()) {
                for (int pow2 : another.unitmap.keySet()) {
                    int pow = pow1 + pow2;
                    BigInteger coe = this.unitmap.get(pow1).multiply(another.unitmap.get(pow2));
                    if (hashMap.containsKey(pow)) {
                        BigInteger coe1 = hashMap.get(pow).add(coe);
                        hashMap.put(pow,coe1);
                    } else {
                        hashMap.put(pow,coe);
                    }
                }
            }
            return new Poly(hashMap);
        }
    }

    //对于表达式的乘方操作，我们可以通过多次调用multiPoly方法来实现 偷懒 hhhhhh
    public Poly powPoly(int pow) {
        if (pow == 0) { //为0时特判 只剩一项1
            HashMap<Integer,BigInteger> hashMap = new HashMap<>();
            hashMap.put(0,BigInteger.ONE);
            return new Poly(hashMap);
        } else {
            Poly poly = new Poly(this.unitmap); //本身表达式
            for (int i = 1; i < pow; i++) {
                poly = poly.multiPoly(this);
            }
            return poly;
        }
    }

    //取反 我们在Term类中定义出了sign，若为-1，则需要对ploy中的所有单项式系数取反
    public void negate() {
        for (Integer pow : this.unitmap.keySet()) {
            BigInteger coe = this.unitmap.get(pow);
            this.unitmap.put(pow,coe.negate());
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        BigInteger zero = new BigInteger("0");
        BigInteger minusone = new BigInteger("-1");
        //单项式系数 0 1 -1 单项式指数 0 1
        for (int pow : this.unitmap.keySet()) {
            BigInteger coe = this.unitmap.get(pow);
            if (pow == 0) {
                if (Objects.equals(coe, zero)) { //系数为0
                    continue;
                } else if (Objects.equals(coe,BigInteger.ONE)) { //系数为1
                    sb.append(1);
                } else if (Objects.equals(coe,minusone)) { //系数为-1
                    sb.append(-1);
                } else {
                    sb.append(coe);
                }
                sb.append('+');
            } else if (pow == 1) {
                if (Objects.equals(coe,zero)) {
                    continue;
                } else if (Objects.equals(coe,BigInteger.ONE)) {
                    sb.append('x');
                } else if (Objects.equals(coe,minusone)) {
                    sb.append("-x");
                } else {
                    sb.append(coe);
                    sb.append("*x");
                }
                sb.append('+');
            } else {
                if (Objects.equals(coe,zero)) {
                    continue;
                } else if (Objects.equals(coe,BigInteger.ONE)) {
                    sb.append("x^");
                    sb.append(pow);
                } else if (Objects.equals(coe,minusone)) {
                    sb.append("-x^");
                    sb.append(pow);
                } else {
                    sb.append(coe);
                    sb.append("*x^");
                    sb.append(pow);
                }
                sb.append('+');
            }
        }
        if (sb.length() == 0) { // 防止输出空串
            return "0";
        } else {
            sb.deleteCharAt(sb.length() - 1);
            Processer processer = new Processer(sb.toString());
            processer.deleteContiniousAddSub();
            processer.sort();
            processer.deletePreAdd();
            return  processer.getInput();
        }
    }
}

