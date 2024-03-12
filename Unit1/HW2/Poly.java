import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class Poly implements Serializable {
    private HashMap<Unit,BigInteger> unitMap; // 存储单元和系数

    public Poly(HashMap<Unit,BigInteger> unitMap) {
        this.unitMap = unitMap;
    }

    public HashMap<Unit, BigInteger> getUnitMap() {
        return this.unitMap;
    }

    //多项式运算方法
    public Poly addPoly(Poly another) {  // 合并同类项
        Iterator<Map.Entry<Unit, BigInteger>> iterator = this.unitMap.entrySet().iterator();
        while (((Iterator<?>) iterator).hasNext()) {
            Map.Entry<Unit,BigInteger> entry = iterator.next();
            Unit unit = entry.getKey();
            BigInteger coe = entry.getValue();
            if (another.unitMap.containsKey(unit)) {
                BigInteger coe1 = another.unitMap.get(unit).add(coe);
                if (Objects.equals(coe1,BigInteger.ZERO)) {
                    Unit unit1 = new Unit(BigInteger.ZERO,new HashMap<>());
                    another.unitMap.remove(unit);
                    if (another.unitMap.containsKey(unit1)) {
                        another.unitMap.put(unit1, coe1.add(another.unitMap.get(unit1)));
                    } else {
                        another.unitMap.put(unit1, coe1);
                    }
                } else {
                    another.unitMap.put(unit, coe1);
                }
            } else {
                another.unitMap.put(unit,coe);
            }
            iterator.remove();
        }
        return another;
    }

    public Poly multiPoly(Poly another) {  // 单元乘法
        if (this.unitMap.isEmpty()) {
            return new Poly(another.unitMap);
        } else {
            //这个空多项式用于在乘法中进行每一次乘法之后进行多项式合并的辅助手段
            HashMap<Unit, BigInteger> emptyMap = new HashMap<>();
            Poly emptypoly = new Poly(emptyMap);
            HashMap<Unit, BigInteger> hashMap = new HashMap<>();
            for (Unit unit1 : this.unitMap.keySet()) {
                for (Unit unit2 : another.unitMap.keySet()) {
                    BigInteger coe = this.unitMap.get(unit1).multiply(another.unitMap.get(unit2));
                    BigInteger pow = unit1.getPow().add(unit2.getPow());
                    // 合并这里有bug 不能直接putAll 如果有一样的相乘的指数项会丢失
                    HashMap<Poly, BigInteger> expMap = new HashMap<>(unit1.getExpMap());
                    for (Poly poly : unit2.getExpMap().keySet()) {
                        if (expMap.containsKey(poly)) {
                            BigInteger pow1 = expMap.get(poly).add(unit2.getExpMap().get(poly));
                            expMap.put(poly,pow1);
                        } else {
                            expMap.put(poly,unit2.getExpMap().get(poly));
                        }
                    }
                    Unit unit = new Unit(pow,expMap); // 乘法之后的项
                    if (hashMap.containsKey(unit)) {
                        BigInteger coe1 = hashMap.get(unit).add(coe);
                        hashMap.put(unit,coe1);
                    } else {
                        hashMap.put(unit,coe);
                    }
                }
            }
            return new Poly(hashMap);
        }
    }

    //对于表达式的乘方操作，我们可以通过多次调用multiPoly方法来实现 偷懒 hhhhhh
    public Poly powPoly(int pow) {
        if (pow == 0) { //为0时特判 只剩一项1
            HashMap<Unit,BigInteger> unitMap = new HashMap<>();
            Unit unit = new Unit(BigInteger.ZERO,new HashMap<>());
            unitMap.put(unit,BigInteger.ONE);
            return new Poly(unitMap);
        } else {
            Poly poly = new Poly(this.unitMap); //本身表达式
            for (int i = 1; i < pow; i++) {
                poly = poly.multiPoly(this);
            }
            return poly;
        }
    }

    //取反 我们在Term类中定义出了sign，若为-1，则需要对ploy中的所有单项式系数取反
    public void negate() { // 这个有问题
        for (Unit unit : this.unitMap.keySet()) {
            BigInteger coe = this.unitMap.get(unit);
            this.unitMap.put(unit, coe.negate());
        }
    }

    public void multiCoe(BigInteger coe) { // 做系数乘法
        for (Unit unit : this.unitMap.keySet()) {
            BigInteger coe1 = this.unitMap.get(unit);
            this.unitMap.put(unit,coe1.multiply(coe));
        }
    }

    public void divCoe(BigInteger gcd) {
        for (Unit unit : this.unitMap.keySet()) {
            BigInteger coe = this.unitMap.get(unit);
            this.unitMap.put(unit,coe.divide(gcd));
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        BigInteger zero = new BigInteger("0");
        BigInteger minusone = new BigInteger("-1");
        for (Unit unit : this.unitMap.keySet()) {
            sb.append(unit.toString(this));
            sb.append('+');
        }
        if (sb.length() == 0) { // 防止输出空串
            return "0";
        } else {
            sb.deleteCharAt(sb.length() - 1);
            Processer processer = new Processer(sb.toString());
            processer.deleteContiniousAddSub();
            processer.deletePreAdd();
            return processer.getInput();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Poly) {
            Poly poly = (Poly) obj;
            return Objects.equals(unitMap,poly.unitMap);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(unitMap);
    }

    public Poly cloneSerializable() {
        Poly poly = null;
        try {
            //序列化对象
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            //反序列化对象
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            poly = (Poly) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return poly;
    }
}

