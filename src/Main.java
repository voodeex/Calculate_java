import java.util.Scanner;

public class Main {

    static double add(double a, double b) {
        return a + b;
    }

    static double subtract(double a, double b) {
        return a - b;
    }

    static double multiply(double a, double b) {
        return a * b;
    }

    static double divide(double a, double b) {
        if (b == 0) {
            System.out.println("Ошибка: деление на 0!");
            return 0;
        }

        return a / b;
    }

    static double calculate(double a, double b, char operator) {
        switch (operator) {
            case '+':
                return add(a, b);

            case '-':
                return subtract(a, b);

            case '*':
                return multiply(a, b);

            case '/':
                return divide(a, b);

            default:
                System.out.println("Неизвестная операция");
                return 0;
        }
    }

    static void printResult(double result) {
        System.out.println("Результат: " + result);
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.print("Введите операцию (+, -, *, /) или q для выхода: ");
            char operator = scanner.next().charAt(0);

            if (operator == 'q') {
                System.out.println("Выход");
                break;
            }

            System.out.print("Введите первое число: ");
            double a = scanner.nextDouble();

            System.out.print("Введите второе число: ");
            double b = scanner.nextDouble();

            double result = calculate(a, b, operator);

            printResult(result);
        }
    }
}