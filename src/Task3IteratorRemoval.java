import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class Task3IteratorRemoval {
    public static void main(String[] args) {
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (int number = 1; number <= 30; number++) {
            numbers.add(number);
        }
        Iterator<Integer> numberIterator = numbers.iterator();
        while (numberIterator.hasNext()) {
            if (numberIterator.next() % 2 == 0) {
                numberIterator.remove();
            }
        }
        System.out.println("После удаления чётных через Iterator: " + numbers);

        ArrayList<String> names = new ArrayList<String>();
        names.add("Анна");
        names.add("Иван");
        names.add("Олег");
        names.add("Мария");
        names.add("Александр");
        names.add("Ева");
        names.add("Дмитрий");

        Iterator<String> nameIterator = names.iterator();
        while (nameIterator.hasNext()) {
            if (nameIterator.next().length() <= 4) {
                nameIterator.remove();
            }
        }
        System.out.println("Длинные имена: " + names);

        ArrayList<Integer> unsafe = new ArrayList<Integer>();
        unsafe.add(1);
        unsafe.add(2);
        unsafe.add(3);
        try {
            // Изменение списка через remove во время for-each нарушает работу итератора.
            for (Integer number : unsafe) {
                if (number == 1) {
                    unsafe.remove(number);
                }
            }
        } catch (ConcurrentModificationException exception) {
            System.out.println("Ожидаемая ошибка при удалении в for-each: "
                    + exception.getClass().getSimpleName());
        }

        ArrayList<Integer> modern = new ArrayList<Integer>();
        for (int number = 1; number <= 30; number++) {
            modern.add(number);
        }
        modern.removeIf(number -> number % 2 == 0);
        System.out.println("Результат removeIf совпадает: " + modern.equals(numbers));
    }
}
