public class InputHandler {
    private final String input;

    public InputHandler(String input) {
        this.input = input;
    }

    public void process() {
        Processer processer = new Processer(this.input);
        processer.deleteBlank();//运行后面的逻辑前一定要先删除空白字符
        processer.deleteContiniousAddSub();
        processer.deletePreAdd();
        processer.deletePreZero();
        //System.out.println(processer.getInput());
        Lexer lexer = new Lexer(processer.getInput());
        Parser parser = new Parser(lexer);
        Expr expr = parser.parseExpr();
        System.out.println(expr.toPoly().toString());
    }
}
