import java.util.ArrayList;

public class Lexer {
    private final String input;
    private int pos;
    private TokenType tokenType; //将tokenType送给parser会显得更加优雅
    private String tokenNumber;
    private ArrayList<String> tokens;
    private ArrayList<TokenType> tokenTypes;

    public Lexer(String input) {
        this.input = input;
        this.tokenType = TokenType.NULL;
        this.tokenNumber = null;
        this.pos = 0;
        this.tokens = new ArrayList<>();
        this.tokenTypes = new ArrayList<>();
        this.next();
    }

    //得到完整的数字
    private String getNumber() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            ++pos;
        }
        return sb.toString();//返回字符串
    }

    //获取下一个token 喂给parser
    public void next() {
        if (pos == input.length()) { //是空串
            return;
        }
        char c = input.charAt(pos);
        if (Character.isDigit(c)) {
            this.tokenType = TokenType.NUM;
            this.tokenNumber = getNumber();
            tokens.add(this.tokenNumber);
            tokenTypes.add(TokenType.NUM);
        } else {
            switch (c) {
                case '+' :
                    pos++;
                    this.tokenType = TokenType.ADD;
                    tokens.add("+");
                    tokenTypes.add(TokenType.ADD);
                    break;
                case '-' :
                    pos++;
                    this.tokenType = TokenType.SUB;
                    tokens.add("-");
                    tokenTypes.add(TokenType.SUB);
                    break;
                case '*' :
                    pos++;
                    this.tokenType = TokenType.MULTI;
                    tokens.add("*");
                    tokenTypes.add(TokenType.MULTI);
                    break;
                case '^' :
                    pos++;
                    this.tokenType = TokenType.EXP;
                    tokens.add("^");
                    tokenTypes.add(TokenType.EXP);
                    break;
                case '(' :
                    pos++;
                    this.tokenType = TokenType.LP;
                    tokens.add("(");
                    tokenTypes.add(TokenType.LP);
                    break;
                case ')' :
                    pos++;
                    this.tokenType = TokenType.RP;
                    tokens.add(")");
                    tokenTypes.add(TokenType.RP);
                    break;
                case 'x' :
                    pos++;
                    this.tokenType = TokenType.X;
                    tokens.add("X");
                    tokenTypes.add(TokenType.X);
                    break;
                default:
            }
        }
    }

    public TokenType getCurTokenType() {
        return this.tokenType;
    }

    public String getTokenNumber() {
        return this.tokenNumber;
    }
}
