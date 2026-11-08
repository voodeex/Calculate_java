package service;
import model.Animal;

public final class AnimalSorter {
    private AnimalSorter() { }
    public static void sort(Animal[] items, AnimalComparator comparator) {
        for (int i = 1; i < items.length; i++) {
            Animal current = items[i];
            int j = i - 1;
            while (j >= 0 && comparator.compare(items[j], current) > 0) {
                items[j + 1] = items[j];
                j--;
            }
            items[j + 1] = current;
        }
    }
    public static final class ByName implements AnimalComparator {
        @Override public int compare(Animal a, Animal b) {
            return a.getName().compareToIgnoreCase(b.getName());
        }
    }
    public static final class ByWeight implements AnimalComparator {
        @Override public int compare(Animal a, Animal b) {
            return Double.compare(a.getWeight(), b.getWeight());
        }
    }
}
