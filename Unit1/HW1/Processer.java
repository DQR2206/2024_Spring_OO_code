
public class Processer {
    private String input;

    public Processer(String input) {
        this.input = input;
    }

    //处理空格
    public void deleteBlank() {
        this.input = this.input.replaceAll("[ |\\t]", "");
    }

    //去掉连续的正负号 去掉连续的正负号即为该项确定出正负,减掉连续+-后，字符串的长度会变化，可能此时的i已经超出了长度范围
    public void deleteContiniousAddSub() {
        int i;
        for (i = 0;i < this.input.length(); i++) {
            if (this.input.charAt(i) == '+' || this.input.charAt(i) == '-') {
                int tail = i;
                int cntMinus = 0;
                while (tail < this.input.length() && this.input.charAt(tail) == '+' ||
                        tail < this.input.length() && this.input.charAt(tail) == '-')
                {
                    if (this.input.charAt(tail) == '-') {
                        cntMinus++;
                    }
                    tail++;
                }
                if (cntMinus % 2 == 1) {
                    this.input = this.input.substring(0, i) + "-" + this.input.substring(tail);
                } else {
                    this.input = this.input.substring(0, i) + "+" + this.input.substring(tail);
                }
            }
        }
    }

    //处理冗余的正负号 其中1-4属于题目规定中可以发生冗余的正号
    public void deletePreAdd() {
        //1.去掉首项前的正号
        if (this.input.charAt(0) == '+') {
            this.input = this.input.substring(1);
        }
        int i = 0;
        //2.去掉乘号后的正号（乘号后为第一个因子，前可有符号）
        for (i = 0;i < this.input.length(); i++) {
            if (this.input.charAt(i) == '*' && this.input.charAt(i + 1) == '+') {
                this.input = this.input.substring(0,i + 1) + this.input.substring(i + 2);
            }
        }
        //3.去掉左括号后的正号（表达式因子的首项正号）
        for (i = 0;i < this.input.length(); i++) {
            if (this.input.charAt(i) == '(' && this.input.charAt(i + 1) == '+') {
                this.input = this.input.substring(0, i + 1) + this.input.substring(i + 2);
            }
        }
        //4.去掉指数中的正号（指数非负）
        for (i = 0;i < this.input.length(); i++) {
            if (this.input.charAt(i) == '^' && this.input.charAt(i + 1) == '+') {
                this.input = this.input.substring(0,i + 1) + this.input.substring(i + 2);
            }
        }
    }

    //处理前导零 00000 000001
    //首先获取连续数字的长度，然后比较0的长度，相等则保留一个0，否则删除前导0,这里关于数字只检查开头为0的
    public void deletePreZero() {
        for (int i = 0;i < this.input.length();i++) {
            if (this.input.charAt(i) == '0' && i == 0
                    || i >= 1 && this.input.charAt(i) == '0'
                    && !Character.isDigit(this.input.charAt(i - 1))) {
                int tail = i;
                while (tail < this.input.length() && this.input.charAt(tail) == '0') {
                    tail++;
                }
                int zlength = tail - i;
                tail = i;
                while (tail < this.input.length() && Character.isDigit(this.input.charAt(tail))) {
                    tail++;
                }
                int numberlength = tail - i;
                if (zlength < numberlength) { //满足前导0
                    this.input = this.input.substring(0,i) + this.input.substring(i + zlength);
                } else if (zlength == numberlength) {
                    this.input = this.input.substring(0,i + 1) + this.input.substring(i + zlength);
                }
            }
        }
    }

    public void sort() {
        //最终我们的表达式中的形式为，用+-分割的单项式 因此我们不论如何们只需要检查，找到第一个加号，把这项移动到第一位即可
        // -x+1 -> 1-x
        //先判断是不是负号在最前面
        if (this.input.charAt(0) == '-') {
            int addpos = -1;
            int i = 0;
            for (i = 0;i < this.input.length();i++) {
                if (this.input.charAt(i) == '+') {
                    addpos = i;
                    break;
                }
            }
            if (addpos != -1) { //存在加项 找到这项后的符号，将它分割出来
                for (i = addpos + 1;i < this.input.length();i++) {
                    if (this.input.charAt(i) == '+' || this.input.charAt(i) == '-') {
                        break;
                    }
                }
                this.input = this.input.substring(addpos,i) +
                        this.input.substring(0,addpos) + this.input.substring(i);
            }
        }
    }

    public String getInput() {
        return this.input;
    }
}
