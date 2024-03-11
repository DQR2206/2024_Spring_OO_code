package expr;

import java.util.HashSet;
import java.util.Iterator;

public class Term {
    private final HashSet<Factor> factors;

    public Term() {
        this.factors = new HashSet<>();
    }

    public void addFactor(Factor factor) {
        this.factors.add(factor);
    }

    //用一个项中管理的因子们构建起这个项的后缀表达式 项的原本形式为 ()*()*() 后缀形式为 () () * () * ...
    public String toString() {
        Iterator<Factor> iter = factors.iterator();
        StringBuilder sb = new StringBuilder();
        sb.append(iter.next().toString());
        if (iter.hasNext()) {  //第一二项的后缀形式特殊处理
            sb.append(" ");
            sb.append(iter.next().toString());
            sb.append(" *");
            while (iter.hasNext()) {
                sb.append(" ");
                sb.append(iter.next().toString());
                sb.append(" *");
            }
        }
        return sb.toString();
    }
}
