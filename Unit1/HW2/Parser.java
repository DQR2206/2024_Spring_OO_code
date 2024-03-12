import java.math.BigInteger;
import java.util.ArrayList;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Expr parseExpr() {
        Expr expr = new Expr();
        int sign = 1;
        if (lexer.getCurTokenType() == TokenType.SUB) {
            sign = -1;
            lexer.next();
        } else if (lexer.getCurTokenType() == TokenType.ADD) {
            lexer.next();
        }
        expr.addTerm(parseTerm(sign));
        // () + () - ()
        while (lexer.getCurTokenType() == TokenType.ADD ||
                lexer.getCurTokenType() == TokenType.SUB)
        {
            if (lexer.getCurTokenType() == TokenType.ADD) {
                lexer.next();
                expr.addTerm(parseTerm(1));
            } else {
                lexer.next();
                expr.addTerm(parseTerm(-1));
            }
        }
        return expr;
    }

    // parseExpr将项的符号传入，应该怎么处理？在Term中设置相应的属性
    public Term parseTerm(int sign) {
        Term term = new Term(sign);
        //进行递归调用
        term.addFactor(parseFactor());
        // ()*()*()
        while (lexer.getCurTokenType() == TokenType.MULTI) {
            lexer.next();
            term.addFactor(parseFactor());
        }
        return term;
    }

    public Factor parseFactor() {
        if (lexer.getCurTokenType() == TokenType.LP) {
            lexer.next();
            Expr expr = parseExpr();
            lexer.next();
            if (lexer.getCurTokenType() == TokenType.POW) {
                lexer.next();
                expr.setPow(Integer.parseInt(lexer.getTokenNumber()));
                lexer.next();
            }
            return expr;
        } else if (lexer.getCurTokenType() == TokenType.NUM) {
            BigInteger num = new BigInteger(lexer.getTokenNumber());
            lexer.next();
            return new Number(num);
        } else if (lexer.getCurTokenType() == TokenType.SUB) {
            lexer.next();
            BigInteger num = new BigInteger("-" + lexer.getTokenNumber());
            lexer.next();
            return new Number(num);
        } else if (lexer.getCurTokenType() == TokenType.X) { // x / x^n
            lexer.next();
            if (lexer.getCurTokenType() == TokenType.POW) {
                lexer.next();
                BigInteger num = new BigInteger(lexer.getTokenNumber());
                lexer.next();
                return new Pow(num);
            } else {
                return new Pow(BigInteger.ONE);
            }
        } else if (lexer.getCurTokenType() == TokenType.EXP) { //指数函数
            lexer.next();
            return parseExp();
        } else if (lexer.getCurTokenType() == TokenType.F ||
                lexer.getCurTokenType() == TokenType.G ||
                lexer.getCurTokenType() == TokenType.H) {
            return parseFunc(lexer.getCurTokenType().toString().toLowerCase()); // 传递小写字母
        } else {
            return null;
        }
    }

    public Factor parseExp() { // exp(<factor>)^<num> | exp(<factor>)
        // lexer : exp -> pos = pos + 4 already into the (
        // 读到RP时接着往后读看有没有指数
        BigInteger pow = BigInteger.ONE;//默认指数为1
        Factor innerFactor = parseFactor();
        lexer.next();
        if (lexer.getCurTokenType() == TokenType.POW) {
            lexer.next();
            pow = new BigInteger(lexer.getTokenNumber());
            lexer.next();
        }
        return new Exp(innerFactor, pow);
    }

    public Factor parseFunc(String name) {
        //函数调用的形式为 f(<factor>,<factor>,<factor>) 进来时正读到f
        lexer.next(); //读f-LP
        lexer.next(); //跳过f-LP
        ArrayList<Factor> actualParams = new ArrayList<>();  //实参列表要用顺序表
        actualParams.add(parseFactor());
        while (lexer.getCurTokenType() != TokenType.RP) {
            lexer.next();
            actualParams.add(parseFactor());
        }
        lexer.next(); //跳过f-RP
        return new Func(name, actualParams);
    }
}
