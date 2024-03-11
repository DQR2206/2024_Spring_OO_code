package expression;

import java.util.ArrayList;

public class Expr {

    //表达式类管理多个项
    private ArrayList<Term> terms = new ArrayList<>();

    public void addTerm(Term term) {
        terms.add(term);
    }

    //将表达式转化为后缀表达式输出
    @Override
    public String toString() {
        if (terms.size() == 1) {
            // only one term for example 1*2*3
            return terms.get(0).toString();
        } else {
            // many terms for example 1*2*3 + 4*5*6
            //first turn terms into postfix notation
            //1 2 * 3 * and 4 5 * 6 *
            //then turn the whole expression into postfix notation
            //1 2 * 3 * 4 5 * 6 * +
            StringBuilder sb = new StringBuilder();
            sb.append(terms.get(0));
            sb.append(" ");
            /* TODO */
            sb.append(terms.get(1));
            sb.append(" ");
            sb.append("+");
            for (int i = 2; i < terms.size(); i++) {
                sb.append(" ");
                sb.append(terms.get(i));
                sb.append(" ");
                sb.append("+");
            }
            return sb.toString();
        }
    }
}
