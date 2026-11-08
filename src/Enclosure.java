import java.util.Arrays;

public class Enclosure {
    private final int id;
    private final String title;
    private final int capacity;
    private int[] animalIds;
    private int size;

    public Enclosure(int id, String title, int capacity) {
        if (id <= 0 || title == null || title.trim().isEmpty() || capacity <= 0) {
            throw new IllegalArgumentException("Некорректные данные вольера");
        }
        this.id = id;
        this.title = title.trim();
        this.capacity = capacity;
        this.animalIds = new int[Math.min(4, capacity)];
    }

    public int getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean contains(int animalId) {
        return indexOf(animalId) >= 0;
    }

    public void add(int animalId) throws ZooException {
        if (contains(animalId)) {
            return;
        }
        if (size >= capacity) {
            throw new ZooException("Вольер " + id + " заполнен");
        }
        if (size == animalIds.length) {
            int newLength = Math.min(capacity, Math.max(1, animalIds.length * 2));
            animalIds = Arrays.copyOf(animalIds, newLength);
        }
        animalIds[size++] = animalId;
    }

    public void remove(int animalId) {
        int index = indexOf(animalId);
        if (index < 0) {
            return;
        }
        for (int i = index; i < size - 1; i++) {
            animalIds[i] = animalIds[i + 1];
        }
        size--;
    }

    private int indexOf(int animalId) {
        for (int i = 0; i < size; i++) {
            if (animalIds[i] == animalId) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return String.format("Вольер{id=%d, название='%s', занято=%d/%d}",
                id, title, size, capacity);
    }
}
