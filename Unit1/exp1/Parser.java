import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private  final Pattern numberPattern = Pattern.compile("[0-9a-zA-Z]+"); // TODO
    private final HashMap<String, Integer> parameters; // x 1 y 2
    
    public Parser(HashMap<String, Integer> parameters) {
        this.parameters = parameters;
    }
    
    public Operator parse(String expression) {
        int position = findAddOrSub(expression);
        if (position != -1) {
            if (expression.charAt(position) == '+') {
                return new Add(parse(expression.substring(0, position)),
                        parse(expression.substring(position + 1)));
            } else {
                return new Sub(parse(expression.substring(0, position)),
                        parse(expression.substring(position + 1)));
            }
        } else { // x*y
            position = findMul(expression);
            if (position != -1) {
                return new Mul(parse(expression.substring(0, position))
                        , parse(expression.substring(position + 1)));
            } else { // x
                if (!expression.equals("")) { //表达式不是空
                    Matcher matcher = numberPattern.matcher(expression);
                    // TODO 提示：这里正则表达式捕获到的可能是数字或变量
                    matcher.find();
                    String str = matcher.group(0);
                    boolean flag = false;
                    for (int i = 0; i < str.length();i++) {
                        if(Character.isUpperCase(str.charAt(i))||Character.isLowerCase(str.charAt(i))) {
                            flag = true;
                        }
                    }
                    if (flag) {
                        int key = parameters.get(str);
                        return new Num(key);
                    } else {
                        return new Num(Integer.parseInt(str));
                    }
                } else {
                    return new Num(0);
                }
            }
        }
    }

    private int findAddOrSub(String expression) {
        int position = -1;
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '+' || expression.charAt(i) == '-') {
                position = i;
            }
        }
        return position;
    }

    private int findMul(String expression) {
        int position = -1;
        // TODO
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '*') {
                position = i;
            }
        }
        return position;
    }
}
