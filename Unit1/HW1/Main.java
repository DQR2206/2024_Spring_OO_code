import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        InputHandler inputHandler = new InputHandler(input);
        inputHandler.process();
    }
}
