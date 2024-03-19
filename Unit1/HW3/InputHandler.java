import java.util.Scanner;

public class InputHandler {
    private final Scanner scanner;

    public InputHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    public void process() {
        int n = Integer.parseInt(scanner.nextLine());
        for (int i = 0;i < n;i++) {
            String funcdef = scanner.nextLine();
            Processer processer = new Processer(funcdef);
            processer.deleteBlank();
            processer.deleteContiniousAddSub();
            processer.deletePreAdd();
            processer.deletePreZero();
            Definer.addFunc(processer.getInput());
        }
        String input = scanner.nextLine();
        Processer processer1 = new Processer(input);
        processer1.deleteBlank();//运行后面的逻辑前一定要先删除空白字符
        processer1.deleteContiniousAddSub();
        processer1.deletePreAdd();
        processer1.deletePreZero();
        Lexer lexer = new Lexer(processer1.getInput());
        Parser parser = new Parser(lexer);
        Expr expr = parser.parseExpr();
        Poly poly = expr.toPoly();
        String string = poly.toString();
        Processer processer2 = new Processer(string);
        processer2.deleteContiniousAddSub();
        processer2.deletezerofactor();
        processer2.deletePreAdd();
        System.out.println(processer2.getInput());
    }
}
