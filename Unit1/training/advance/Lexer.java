import java.util.ArrayList;

public class Lexer {

    //词法分析，识别不同的token
    // example: (1+2)*(3+4)
    private final String input;
    private int pos = 0;
    private String curToken;

    private ArrayList<String> tokens;
    private ArrayList<String> tokenTypes;

    public Lexer(String input) {
        this.input = input;
        this.next();
    }

    //该方法用于获取到完整的数字作为token
    private String getNumber() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            ++pos;
        }

        return sb.toString();
    }

    //用于获取下一个token
    public void next() {
        if (pos == input.length()) { //是空串
            return;
        }

        char c = input.charAt(pos);
        if (Character.isDigit(c)) {
            curToken = getNumber();
        } else if (c == '+' ||c == '*' || c == '(' || c == ')' ) {
            pos += 1;
            curToken = String.valueOf(c);
        }
    }

    public String peek() {
        return this.curToken;
    }
}
