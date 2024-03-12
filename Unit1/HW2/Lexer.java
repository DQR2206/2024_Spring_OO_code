
public class Lexer {
    private final String input;
    private int pos;
    private TokenType tokenType; //将tokenType送给parser会显得更加优雅
    private String tokenNumber;

    public Lexer(String input) {
        this.input = input;
        this.tokenType = TokenType.NULL;
        this.tokenNumber = null;
        this.pos = 0;
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
        } else if (c == 'f' || c == 'g' || c == 'h') { //自定义函数
            this.tokenType = TokenType.valueOf(String.valueOf(c).toUpperCase());
            pos++;
        } else if (c == 'e') { //exp()函数
            this.tokenType = TokenType.EXP;
            pos = pos + 4;
        } else {
            switch (c) {
                case '+' :
                    pos++;
                    this.tokenType = TokenType.ADD;
                    break;
                case '-' :
                    pos++;
                    this.tokenType = TokenType.SUB;
                    break;
                case '*' :
                    pos++;
                    this.tokenType = TokenType.MULTI;
                    break;
                case '^' :
                    pos++;
                    this.tokenType = TokenType.POW;
                    break;
                case '(' :
                    pos++;
                    this.tokenType = TokenType.LP;
                    break;
                case ')' :
                    pos++;
                    this.tokenType = TokenType.RP;
                    break;
                case 'x' :
                    pos++;
                    this.tokenType = TokenType.X;
                    break;
                case ',' :
                    pos++;
                    this.tokenType = TokenType.COMMA;
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
