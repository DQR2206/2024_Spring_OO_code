import java.math.BigInteger;

public class Unit {  //单项式类 a*x^b形式
    private BigInteger coe;
    private int pow;

    public Unit(BigInteger coe,int pow) {
        this.coe = coe;
        this.pow = pow;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.coe);
        sb.append('*');
        sb.append('x');
        sb.append('^');
        sb.append(this.pow);
        return sb.toString();
    }

}
