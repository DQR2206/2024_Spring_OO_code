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

}
