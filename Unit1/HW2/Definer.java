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

    // 这个方法目前是有问题的 factor接口的 toString 还不明确
    // 举个例子想一想 如果是 f(x,y,z) = f(1,exp(x),g(x,y))该怎么做？
    // 逻辑梳理 ： 通过parser找到了各个factor，当我们识别到因子 g(x,y)时就会调用parseFunc方法再去解析（类似于自引用）
    // 传上来的时候，g(x,y)已经是解析好的一个Func newFunc expr 这时我们再进行toString 确确实实只要使用下层函数的toFunc属性即可，此时一定是已经解析好的
    // 在这中情况下是不是需要将表达式外加括号？
    // 用g(x,y)去替换函数定义式中的z时，例如 g(x,y) = x+y f(x,y,z) = x*y*z   则替换后 f(x,y,g(x,y)) = x*exp(x)*(x+y)
    //故需要在callFunc后表达式左右加上括号
    // 自定义函数解析时使用 获得替换后的函数表达式
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
