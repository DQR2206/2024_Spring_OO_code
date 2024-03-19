import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;

public class Unit implements Serializable {  //单元 ax^n*exp(<factor>)^n
    private BigInteger pow;
    private HashMap<Poly,BigInteger> expMap; // expMap中的key是factor value是指数

    public Unit(BigInteger pow,HashMap<Poly,BigInteger> expMap) {
        this.pow = pow;
        this.expMap = expMap;
    }

    public BigInteger getPow() {
        return pow;
    }

    public HashMap<Poly,BigInteger> getExpMap() {
        return this.expMap;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Unit) {
            Unit unit = (Unit) obj;
            return Objects.equals(pow,unit.pow)
                    && Objects.equals(expMap,unit.expMap);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(pow,expMap);
    }

    public Unit cloneSerializable() {
        Unit unit = null;
        try {
            //序列化对象
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            //反序列化对象
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            unit = (Unit) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unit;
    }

    public String toString(Poly poly) {
        return Simplify.simplifyUnit(this,poly);
    }

    public Poly derive(BigInteger coe) {
        if (Objects.equals(this.pow,BigInteger.ZERO) && this.expMap.isEmpty()) { // 是数字
            return Unit.deriveNumber();
        } else if (this.pow.compareTo(BigInteger.ZERO) > 0
                && this.expMap.isEmpty()) { //是幂函数 coe*x^pow
            return Unit.derivePow(coe,this.pow);
        } else if (Objects.equals(this.pow,BigInteger.ZERO)
                && !this.expMap.isEmpty()) { // 可以划归为指数函数形式 coe* exp(mergepoly)
            return Unit.deriveExp(this.expMap,coe);
        } else {
            return Unit.deriveAll(coe,this.pow,this.expMap);
        }

    }

    public static Poly deriveNumber() {
        HashMap<Unit,BigInteger> newmap = new HashMap<>();
        Unit unit = new Unit(BigInteger.ZERO,new HashMap<>());
        newmap.put(unit,BigInteger.ZERO);
        return new Poly(newmap);
    }

    public static Poly derivePow(BigInteger coe,BigInteger pow) {
        HashMap<Unit,BigInteger> newmap = new HashMap<>();
        BigInteger newcoe = coe.multiply(pow);
        BigInteger newpow = pow.subtract(BigInteger.ONE);
        Unit unit = new Unit(newpow,new HashMap<>());
        newmap.put(unit,newcoe);
        return new Poly(newmap);
    }

    public static Poly deriveExp(HashMap<Poly,BigInteger> expMap,BigInteger coe) {
        HashMap<Unit,BigInteger> newmap = new HashMap<>();
        Poly mergepoly = new Poly(new HashMap<>());
        for (Poly poly : expMap.keySet()) {
            BigInteger pow = expMap.get(poly);
            Poly poly1 = poly.cloneSerializable();
            poly1.multiCoe(pow);
            mergepoly = mergepoly.addPoly(poly1);
        }
        HashMap<Poly,BigInteger> newexpmap = new HashMap<>();
        newexpmap.put(mergepoly,BigInteger.ONE);
        Unit unit = new Unit(BigInteger.ZERO,newexpmap);
        newmap.put(unit,coe);
        Poly poly = new Poly(newmap);
        return poly.multiPoly(mergepoly.derive());
    }

    // coe*(x^n*exp(<mergepoly>)
    // (x^n*exp(<mergepoly>))' = n*x^n-1*exp(<mergepoly>) + <mergepoly>'*x^n*exp(<mergepoly>)
    // <mergepoly>' = newpoly    x^n*exp(<mergepoly>) = 1 unit poly  --> multi
    //     (unit)'             = n(unit1) + poly*poly
    //                         = n(unit1) + poly
    //
    //                           put    + addpoly
    public static Poly deriveAll(BigInteger coe,BigInteger pow,HashMap<Poly,BigInteger> expMap) {
        Poly mergepoly = new Poly(new HashMap<>());
        for (Poly poly : expMap.keySet()) {
            BigInteger exppow = expMap.get(poly);
            Poly poly1 = poly.cloneSerializable();
            poly1.multiCoe(exppow);
            mergepoly = mergepoly.addPoly(poly1);
        }
        HashMap<Poly,BigInteger> newexpmap = new HashMap<>();
        newexpmap.put(mergepoly,BigInteger.ONE);
        BigInteger newpow = pow.subtract(BigInteger.ONE);
        BigInteger newcoe = coe.multiply(pow);
        Unit unit = new Unit(newpow,newexpmap); // x^n-1*exp(<mergepoly>)
        HashMap<Unit,BigInteger> newmap = new HashMap<>();
        newmap.put(unit,newcoe);
        Poly poly = new Poly(newmap);
        Unit unit1 = new Unit(pow,newexpmap); // x^n*exp(<mergepoly>)
        HashMap<Unit,BigInteger> newmap1 = new HashMap<>();
        newmap1.put(unit1,coe);
        Poly poly1 = new Poly(newmap1);
        poly1 = poly1.multiPoly(mergepoly.derive());
        return poly.addPoly(poly1);
    }
}
