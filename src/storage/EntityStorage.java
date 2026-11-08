package storage;
import model.Entity;
import exception.ZooOperationException;
import java.util.Arrays;

public class EntityStorage {
    private Entity[] items = new Entity[8];
    private int size;

    public int size() { return size; }
    public Entity get(int index) {
        if (index < 0 || index >= size) throw new IllegalArgumentException("Неверный индекс");
        return items[index];
    }
    public Entity find(int id) {
        for (int i = 0; i < size; i++) if (items[i].getId() == id) return items[i];
        return null;
    }
    public void add(Entity entity) {
        if (entity == null) throw new IllegalArgumentException("Пустая сущность");
        if (find(entity.getId()) != null) throw new ZooOperationException("ID уже существует");
        if (size == items.length) items = Arrays.copyOf(items, items.length * 2);
        items[size++] = entity;
    }
    public int nextId() {
        int max = 0;
        for (int i = 0; i < size; i++) max = Math.max(max, items[i].getId());
        if (max == Integer.MAX_VALUE) throw new ZooOperationException("Закончились ID");
        return max + 1;
    }
    @Override public String toString() { return "Объектов: " + size; }
}
