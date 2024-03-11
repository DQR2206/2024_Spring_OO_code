package expression;

import java.util.ArrayList;

public class Term {

    //项类管理因子
    private ArrayList<Integer> factors;

    public Term(String s) {
        factors = new ArrayList<>();
        //用乘法符号拆分项为因子
        String[] factorStrs = s.split("\\*");
        for (String factorStr : factorStrs) {
            factors.add(Integer.parseInt(factorStr));
        }
    }

    //将项转化为后缀表达式输出
    @Override
    public String toString() {
        if (factors.size() == 1) {
            //for example 1 -> 1
            return factors.get(0).toString();
        } else {
            //for example  2*3*4 -> 2 3 * 4 * (postfix notation)
            StringBuilder sb = new StringBuilder();
            sb.append(factors.get(0));
            sb.append(" ");
            /* TODO */
            sb.append(factors.get(1));
            sb.append(" ");
            sb.append("*");
            for (int i = 2; i < factors.size(); i++) {
                sb.append(" ");
                sb.append(factors.get(i));
                sb.append(" ");
                sb.append("*");
            }
            return sb.toString();
        }
    }
}
