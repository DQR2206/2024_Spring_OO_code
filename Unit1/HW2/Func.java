import java.util.ArrayList;

public class Func implements Factor {
    private String newFunc; //实参带入形参位置后的结果
    private Expr expr; // newFunc解析成表达式后的结果

    public Func(String name, ArrayList<Factor> actualParams) {
        this.newFunc = Definer.callFunc(name,actualParams);  //形参替换为实参
        this.expr = this.setExpr(); // 解析成表达式
    }

    private Expr setExpr() {
        Processer processer = new Processer(this.newFunc);
        processer.deleteContiniousAddSub();
        processer.deletePreAdd();
        Lexer lexer = new Lexer(processer.getInput());
        Parser parser = new Parser(lexer);
        return parser.parseExpr();
    }

    @Override
    public String toString() {
        return "(" + this.newFunc + ")"; //这里可能会有问题
    }

    @Override
    public Poly toPoly() {
        return this.expr.toPoly();
    }
}
