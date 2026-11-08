import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Task4ListComparison {
    private static final int SIZE = 100_000;
    private static final int OPERATIONS = 5_000;

    public static void main(String[] args) {
        ArrayList<Integer> arrayList = createArrayList();
        LinkedList<Integer> linkedList = createLinkedList();
        int[] indexes = randomIndexes();

        long arrayGet = measureGet(arrayList, indexes);
        long linkedGet = measureGet(linkedList, indexes);
        long arrayAddFirst = measureAddFirst(createArrayList());
        long linkedAddFirst = measureAddFirst(createLinkedList());
        long arrayRemoveFirst = measureRemoveFirst(createArrayList());
        long linkedRemoveFirst = measureRemoveFirst(createLinkedList());

        print("ArrayList get", arrayGet);
        print("LinkedList get", linkedGet);
        print("ArrayList add(0)", arrayAddFirst);
        print("LinkedList add(0)", linkedAddFirst);
        print("ArrayList remove(0)", arrayRemoveFirst);
        print("LinkedList remove(0)", linkedRemoveFirst);

        System.out.println("Вывод: ArrayList выгоднее для произвольного доступа по индексу. "
                + "LinkedList выгоднее для частых вставок и удалений в начале, потому что ей не нужно "
                + "сдвигать оставшиеся элементы массива.");

        SimpleStack stack = new SimpleStack();
        stack.push("первый");
        stack.push("второй");
        System.out.println("Верхушка стека: " + stack.peek());
        System.out.println("Извлечено: " + stack.pop());
        System.out.println("Стек пуст: " + stack.isEmpty());
    }

    private static ArrayList<Integer> createArrayList() {
        ArrayList<Integer> values = new ArrayList<Integer>(SIZE + OPERATIONS);
        for (int i = 0; i < SIZE; i++) {
            values.add(i);
        }
        return values;
    }

    private static LinkedList<Integer> createLinkedList() {
        LinkedList<Integer> values = new LinkedList<Integer>();
        for (int i = 0; i < SIZE; i++) {
            values.add(i);
        }
        return values;
    }

    private static int[] randomIndexes() {
        Random random = new Random(42);
        int[] indexes = new int[OPERATIONS];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = random.nextInt(SIZE);
        }
        return indexes;
    }

    private static long measureGet(List<Integer> list, int[] indexes) {
        long started = System.nanoTime();
        long checksum = 0;
        for (int index : indexes) {
            checksum += list.get(index);
        }
        if (checksum < 0) {
            System.out.println(checksum);
        }
        return System.nanoTime() - started;
    }

    private static long measureAddFirst(List<Integer> list) {
        long started = System.nanoTime();
        for (int i = 0; i < OPERATIONS; i++) {
            list.add(0, -i);
        }
        return System.nanoTime() - started;
    }

    private static long measureRemoveFirst(List<Integer> list) {
        long started = System.nanoTime();
        for (int i = 0; i < OPERATIONS; i++) {
            list.remove(0);
        }
        return System.nanoTime() - started;
    }

    private static void print(String title, long nanoseconds) {
        System.out.printf("%-22s: %.3f мс%n", title, nanoseconds / 1_000_000.0);
    }

    private static class SimpleStack {
        private final LinkedList<String> values = new LinkedList<String>();

        void push(String value) {
            values.addFirst(value);
        }

        String pop() {
            if (values.isEmpty()) {
                throw new IllegalStateException("Стек пуст");
            }
            return values.removeFirst();
        }

        String peek() {
            if (values.isEmpty()) {
                throw new IllegalStateException("Стек пуст");
            }
            return values.getFirst();
        }

        boolean isEmpty() {
            return values.isEmpty();
        }
    }
}
