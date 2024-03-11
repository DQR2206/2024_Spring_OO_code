package expr;

import java.util.HashSet;
import java.util.Iterator;

public class Expr implements Factor {
    //HashSet 不允许有重复元素的集合
    //表达式中管理不同的项
    //

    private final HashSet<Term> terms;

    public Expr() {
        this.terms = new HashSet<>();
    }

    public void addTerm(Term term) {
        this.terms.add(term);
    }

    //关于表达式层级上的形式 (term) + (term) + (term) ...
    //后缀表达式形式应该为 (term) (term) + (term) + (term) + ...同样第一项第二项特殊处理
    public String toString() {
        Iterator<Term> iter = terms.iterator();
        StringBuilder sb = new StringBuilder();
        sb.append(iter.next().toString());
        if (iter.hasNext()) {
            sb.append(" ");
            sb.append(iter.next().toString());
            sb.append(" +");
            while (iter.hasNext()) {
                sb.append(" ");
                sb.append(iter.next().toString());
                sb.append(" +");
            }
        }
        return sb.toString();
    }
}
