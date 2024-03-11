import expr.Expr;

import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();

        Lexer lexer = new Lexer(input); //词法分析
        Parser parser = new Parser(lexer); //语法分析

        Expr expr = parser.parseExpr();
        System.out.println(expr);
    }
}
