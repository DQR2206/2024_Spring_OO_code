import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;

public class Simplify {

    public static String simplifyUnit(Unit unit,Poly poly) {
        int count = Simplify.getValidExp(unit);
        if (Objects.equals(poly.getUnitMap().get(unit), BigInteger.ZERO)) {
            return "0";
        } else if (count == 0) {
            return Simplify.zeroValid(unit,poly);
        } else if (count == 1) {
            return Simplify.oneValid(unit,poly);
        } else {
            return Simplify.moreValid(unit,poly);
        }
    }

    public static int getValidExp(Unit unit) {
        HashMap<Poly, BigInteger> expMap = unit.getExpMap();
        int count = 0;
        for (Poly poly : expMap.keySet()) {
            BigInteger exp = expMap.get(poly);
            if (exp.compareTo(BigInteger.ZERO) > 0) {
                count++;
            }
        }
        return count;
    }

    public static String zeroValid(Unit unit,Poly poly) {
        BigInteger coe = poly.getUnitMap().get(unit);
        BigInteger pow = unit.getPow();
        BigInteger minusone = new BigInteger("-1");
        if (Objects.equals(coe,BigInteger.ONE)) {
            if (Objects.equals(pow, BigInteger.ZERO)) {
                return "1";
            } else if (Objects.equals(pow, BigInteger.ONE)) {
                return "x";
            } else {
                return "x^" + pow;
            }
        } else if (Objects.equals(coe, minusone)) {
            if (Objects.equals(pow, BigInteger.ZERO)) {
                return "-1";
            } else if (Objects.equals(pow,BigInteger.ONE)) {
                return "-x";
            } else {
                return "-x^" + pow;
            }
        } else {
            if (Objects.equals(pow, BigInteger.ZERO)) {
                return coe.toString();
            } else if (Objects.equals(pow, BigInteger.ONE)) {
                return coe + "*x";
            } else {
                return coe + "*x^" + pow;
            }
        }
    }

    public static String oneValid(Unit unit,Poly inputpoly) {
        HashMap<Poly, BigInteger> expMap = unit.getExpMap();
        Poly poly = null;
        BigInteger exp = BigInteger.ZERO;
        for (Poly p : expMap.keySet()) {
            if (expMap.get(p).compareTo(BigInteger.ZERO) > 0) {
                poly = p.cloneSerializable();
                exp = expMap.get(poly);
                break;
            }
        }
        String mono = Simplify.getMono(unit,inputpoly);
        String string = poly.toString();
        if (Simplify.isNumber(string) && new BigInteger(string).compareTo(BigInteger.ZERO) == 0) {
            if (mono.contains("*")) { // *1省略
                return mono.substring(0,mono.length() - 1);
            } else {
                return mono + "1";
            }
        } else {
            BigInteger gcd = Simplify.getGcd(poly); // gcd同时为提到外边的指数
            if (!Objects.equals(gcd, BigInteger.ONE)) {
                poly.divCoe(gcd);
                if (Simplify.addPar(poly)) {
                    return mono + "exp((" + poly.toString() + "))^" + gcd.multiply(exp);
                } else {
                    return mono + "exp(" + poly.toString() + ")^" + gcd.multiply(exp);
                }
            } else {
                if (Simplify.addPar(poly)) {
                    if (Objects.equals(exp,BigInteger.ONE)) {
                        return mono + "exp((" + poly.toString() + "))";
                    } else {
                        return mono + "exp((" + poly.toString() + "))^" + exp;
                    }
                } else {
                    if (Objects.equals(exp,BigInteger.ONE)) {
                        return mono + "exp(" + poly.toString() + ")";
                    } else {
                        return mono + "exp(" + poly.toString() + ")^" + exp;
                    }
                }
            }
        }
    }

    public static boolean isNumber(String s) {
        for (int i = 0;i < s.length();i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String getMono(Unit unit,Poly poly) { // oneValid中调用 获取单项式 a*x^b的部分
        BigInteger coe = poly.getUnitMap().get(unit);
        BigInteger pow = unit.getPow();
        BigInteger minusone = new BigInteger("-1");
        if (Objects.equals(coe, BigInteger.ONE) && Objects.equals(pow, BigInteger.ZERO)) {
            return "";
        } else if (Objects.equals(coe,minusone) && Objects.equals(pow,BigInteger.ZERO)) {
            return "-";
        } else if (Objects.equals(coe, BigInteger.ONE) && Objects.equals(pow, BigInteger.ONE)) {
            return "x*";
        } else if (Objects.equals(coe,minusone) && Objects.equals(pow,BigInteger.ONE)) {
            return "-x*";
        } else if (Objects.equals(coe, BigInteger.ONE)) {
            return "x^" + pow + '*';
        } else if (Objects.equals(coe, new BigInteger("-1"))) {
            return "-x^" + pow + '*';
        } else if (Objects.equals(pow, BigInteger.ZERO)) {
            return coe.toString() + '*';
        } else if (Objects.equals(pow, BigInteger.ONE)) {
            return coe + "*x*";
        } else {
            return coe + "*x^" + pow + '*';
        }
    }

    public static String moreValid(Unit unit,Poly inputpoly) {
        HashMap<Poly, BigInteger> expMap = Simplify.getValidExpMap(unit); //深克隆指数表
        HashMap<Poly,BigInteger> newMap = new HashMap<>();
        String mono = Simplify.getMono(unit,inputpoly);
        HashMap<Unit, BigInteger> unitMap = new HashMap<>();
        Poly result = new Poly(unitMap);
        for (Poly poly : expMap.keySet()) {
            Poly newPoly = poly.cloneSerializable();
            newPoly.multiCoe(expMap.get(poly));
            if (newMap.containsKey(newPoly)) {
                Poly poly1 = newPoly.cloneSerializable();
                poly1.multiCoe(newMap.get(newPoly).add(BigInteger.ONE));
                newMap.put(poly1, BigInteger.ONE);
                newMap.remove(newPoly);
            } else {
                newMap.put(newPoly, BigInteger.ONE);
            }
        }
        // 合并同类项 加法关系 em1*unit1 + em2*unit2 = (em1+em2)*unit1
        for (Poly poly : newMap.keySet()) {
            result = result.addPoly(poly);
        }
        // result 为新的指数中的多项式 对多项式中的unit提取公因数 放到外边
        BigInteger gcd = Simplify.getGcd(result); // gcd同时为提到外边的指数
        if (!Objects.equals(gcd, BigInteger.ONE)) {
            result.divCoe(gcd);
            if (Simplify.addPar(result)) {
                return mono + "exp((" + result.toString() + "))^" + gcd;
            } else {
                return mono + "exp(" + result.toString() + ")^" + gcd;
            }
        } else {
            if (Simplify.addPar(result)) {
                return mono + "exp((" + result.toString() + "))";
            } else {
                return mono + "exp(" + result.toString() + ")";
            }
        }
    }

    public static BigInteger getGcd(Poly poly) {
        BigInteger gcd = BigInteger.ZERO;
        for (Unit unit : poly.getUnitMap().keySet()) {
            BigInteger coe = poly.getUnitMap().get(unit);
            if (gcd.compareTo(BigInteger.ZERO) == 0) {  // 先赋值为第一个系数
                gcd = coe;
            } else {
                gcd = gcd.gcd(coe); // 获得第一个系数与第二个系数的最大公因数
            }
        }
        if (gcd.compareTo(BigInteger.ZERO) == 0) {
            return BigInteger.ONE;
        } else {
            return gcd.abs();
        }
    }

    public static HashMap<Poly,BigInteger> getValidExpMap(Unit unit) {
        HashMap<Poly,BigInteger> expMap = new HashMap<>();
        for (Poly poly : unit.getExpMap().keySet()) {
            BigInteger exp = unit.getExpMap().get(poly);
            if (exp.compareTo(BigInteger.ZERO) > 0) {
                Poly poly1 = poly.cloneSerializable();
                expMap.put(poly1,exp);
            }
        }
        return expMap;
    }

    public static boolean addPar(Poly poly) {
        if (poly.getUnitMap().size() >= 2) { // 两项及以上一定要加括号
            return true;
        } else if (poly.getUnitMap().size() == 1) {
            for (Unit unit : poly.getUnitMap().keySet()) {
                BigInteger coe = poly.getUnitMap().get(unit);
                BigInteger pow = unit.getPow();
                HashMap<Poly, BigInteger> expMap = unit.getExpMap();
                if (Objects.equals(coe, BigInteger.ZERO)) {
                    return false;
                } else {
                    if (Objects.equals(pow, BigInteger.ZERO) && expMap.isEmpty()) { //对应数字
                        return false;
                    } else if (Objects.equals(coe, BigInteger.ONE) && expMap.isEmpty()) { // 对应幂函数
                        return false;
                    } else if (Objects.equals(coe, BigInteger.ONE)
                            && Objects.equals(pow, BigInteger.ZERO)
                            && Simplify.getValidExp(unit) == 1) { // 对应指数函数
                        return false;
                    }
                }
            }
        }
        return true;
    }
}

