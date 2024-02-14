import java.util.Scanner;

public class Myscanner {
    private Scanner scanner;
    //构造方法
    public Myscanner(Scanner scanner) {
        this.scanner = scanner;
    }
    public void scan() {
        String[] a = scanner.nextLine().split(" +");
        double length = Double.parseDouble(a[0]);
        double width = Double.parseDouble(a[1]);
        double height = Double.parseDouble(a[2]);
        int n = Integer.parseInt(scanner.nextLine());
        for(int i = 0; i < n; i++) {
            String[] strings = scanner.nextLine().split(" +");
            int op = Integer.parseInt(strings[0]);
            if(op == 1) {
                System.out.println(length);
            } else if (op == 2) {
                System.out.println(width);
            } else if (op == 3) {
                System.out.println(height);
            } else if(op == 4) {
                length = Double.parseDouble(strings[1]);
            } else if (op == 5) {
                width = Double.parseDouble(strings[1]);
            } else if (op == 6) {
                height = Double.parseDouble(strings[1]);
            } else if (op == 7) {
                System.out.println(length * width * height);
            }
        }
    }
}
