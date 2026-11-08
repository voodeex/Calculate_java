package ui;
import java.util.Scanner;
import java.time.LocalDate;
import model.Entity;

public class Input {
    private final Scanner scanner = new Scanner(System.in);

    public static class Closed extends RuntimeException { }

    private String line(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) throw new Closed();
        return scanner.nextLine().trim();
    }
    public String text(String prompt) {
        while (true) {
            try { return Entity.text(line(prompt)); }
            catch (IllegalArgumentException e) { System.out.println(e.getMessage()); }
        }
    }
    public int integer(String prompt, int min, int max) {
        while (true) {
            try {
                int value = Integer.parseInt(line(prompt));
                if (value < min || value > max) throw new IllegalArgumentException();
                return value;
            } catch (IllegalArgumentException e) {
                System.out.println("Введите целое число от " + min + " до " + max);
            }
        }
    }
    public double decimal(String prompt, double min, double max) {
        while (true) {
            try { return Entity.number(Double.parseDouble(line(prompt).replace(',', '.')), min, max); }
            catch (IllegalArgumentException e) {
                System.out.println("Введите число от " + min + " до " + max);
            }
        }
    }
    public LocalDate date(String prompt) {
        while (true) {
            try {
                LocalDate date = LocalDate.parse(line(prompt));
                if (date.isAfter(LocalDate.now())) throw new IllegalArgumentException();
                return date;
            } catch (java.time.DateTimeException e) {
                System.out.println("Введите существующую дату в формате ГГГГ-ММ-ДД");
            } catch (IllegalArgumentException e) {
                System.out.println("Дата не должна находиться в будущем");
            }
        }
    }
    public Object choose(String title, Object[] values) {
        System.out.println(title);
        for (int i = 0; i < values.length; i++) System.out.println((i + 1) + ". " + values[i]);
        return values[integer("> ", 1, values.length) - 1];
    }
}
