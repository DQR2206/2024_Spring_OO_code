import expr.Expr;
import expr.Factor;
import expr.Number;
import expr.Term;

import java.math.BigInteger;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    //最外层的表达式形式应当为 () + () * () * () + ...乘积项相加的形式
    //(1+2)*(3+4)
    public Expr parseExpr() {
        Expr expr = new Expr();
        //分析表達式->分析項->分析因子 進行遞歸調用
        expr.addTerm(parseTerm());

        while (lexer.peek().equals("+")) {
            lexer.next();
            expr.addTerm(parseTerm());
        }
        return expr;
    }

    public Term parseTerm() {
        Term term = new Term();
        //进行递归调用
        term.addFactor(parseFactor());

        while (lexer.peek().equals("*")) {
            lexer.next();
            term.addFactor(parseFactor());
        }
        return term;
    }

    // 1*2
    public Factor parseFactor() {
        if (lexer.peek().equals("(")) {    //表达式因子
            lexer.next();
            Factor expr = parseExpr();
            /* TODO */
            lexer.next();
            return expr;
        } else {                          //数字因子
            /* TODO */
            BigInteger num = new BigInteger(lexer.peek());
            lexer.next();//调用.next读取下一个token 按照上面的简单例子即读取到* 之后会再次调用 parsefactor
            return new Number(num);
        }
    }
}
