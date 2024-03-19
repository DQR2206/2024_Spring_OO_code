import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Definer {
    private static HashMap<String,String> funcMap = new HashMap<>(); // funcName -> funcDef
    private static HashMap<String, ArrayList<String>> paraMap = new HashMap<>();

    public static void addFunc(String string) { // 传入终端输入的函数定义式
        // f(x,y,z) = ...
        // 可以考虑将形参 xyz 替换为 q w r
        String input = string;
        for (int i = 0;i < input.length();i++) {
            if (input.charAt(i) == 'x') {
                if (i == 0 || input.charAt(i - 1) != 'e') {
                    input = input.substring(0, i) + "q" + input.substring(i + 1);
                }
            } else if (input.charAt(i) == 'y') {
                input = input.substring(0,i) + "w" + input.substring(i + 1);
            } else if (input.charAt(i) == 'z') {
                input = input.substring(0,i) + "r" + input.substring(i + 1);
            }
        }
        String[] funcCut = input.split("=");
        String funcHead = funcCut[0]; //函数名以及参数列表
        String funcDef = funcCut[1]; //这个就是表达式
        String funcName = funcHead.split("\\(")[0];
        funcMap.put(funcName,funcDef);
        String para = funcHead.split("\\(")[1].split("\\)")[0];
        String[] paraCut = para.split(",");
        ArrayList<String> paraList = new ArrayList<>(Arrays.asList(paraCut));
        paraMap.put(funcName,paraList);
    }

    public static String callFunc(String funcName, ArrayList<Factor> actualParas) {
        String funcDef = funcMap.get(funcName);
        ArrayList<String> formalParas = paraMap.get(funcName);
        // formalParam -> actualParam 有顺序对应关系
        // 例如 f(x,y,z) = x+y+z  f(1,exp(x),g(x,y)) = 1+exp(x)+g(x,y)
        int qindex = formalParas.indexOf("q");
        int windex = formalParas.indexOf("w");
        int rindex = formalParas.indexOf("r");
        for (int i = 0;i < funcDef.length(); i++) {
            if (funcDef.charAt(i) == 'w') {
                funcDef = funcDef.substring(0,i)
                        + "(" + actualParas.get(windex).toString() + ")"
                        +  funcDef.substring(i + 1);
                i = i + actualParas.get(windex).toString().length() + 1;
            } else if (funcDef.charAt(i) == 'q') {
                funcDef = funcDef.substring(0,i)
                        + "(" + actualParas.get(qindex).toString() + ")"
                        + funcDef.substring(i + 1);
                i = i + actualParas.get(qindex).toString().length() + 1;
            } else if (funcDef.charAt(i) == 'r') {
                funcDef = funcDef.substring(0,i)
                        + "(" + actualParas.get(rindex).toString() + ")"
                        + funcDef.substring(i + 1);
                i = i + actualParas.get(rindex).toString().length() + 1;
            }
        }
        return funcDef;
    }

}



