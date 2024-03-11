import java.math.BigInteger;

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

    public Factor parseFactor() {  //对于因子 每个都要考虑幂函数的情况
        if (lexer.getCurTokenType() == TokenType.LP) { //递归调用
            //跳过LP
            lexer.next();
            Expr expr = parseExpr();
            //跳过RP
            lexer.next();
            //如果是 ^
            if (lexer.getCurTokenType() == TokenType.EXP) {
                lexer.next();
                expr.setPow(Integer.parseInt(lexer.getTokenNumber()));
                lexer.next();
            }
            return expr;
        } else if (lexer.getCurTokenType() == TokenType.NUM) { //对数字进行处理 number ^ number
            BigInteger num = new BigInteger(lexer.getTokenNumber());
            //跳过数字 这里的数字保证了只存在n的形式 而不存在指数形式
            lexer.next();
            return new Number(num);
        } else if (lexer.getCurTokenType() == TokenType.SUB) {
            lexer.next();
            BigInteger num = new BigInteger("-" + lexer.getTokenNumber());
            lexer.next();
            return new Number(num);
        } else { //对乘方进行处理 x ^ n 即为读到x
            lexer.next(); // 判断是不是'^' 若读到说明后面还有数字 若没有读到 则说明是x
            if (lexer.getCurTokenType() == TokenType.EXP) {
                lexer.next(); // NUM
                int num = Integer.parseInt(lexer.getTokenNumber());
                lexer.next();
                return new Pow(num);
            } else {
                return new Pow(1);
            }
        }
    }
}
