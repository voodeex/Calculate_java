import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Task2ArrayList {
    public static void main(String[] args) {
        ArrayList<String> cities = new ArrayList<String>();
        cities.add("Москва");
        cities.add("Казань");
        cities.add("Омск");
        cities.add("Тула");
        cities.add("Пермь");
        cities.add("Самара");
        cities.add("Сочи");
        cities.add("Уфа");
        cities.add("Тверь");
        cities.add("Курск");

        System.out.println("Обычный for:");
        for (int i = 0; i < cities.size(); i++) {
            System.out.println(i + ": " + cities.get(i));
        }
        System.out.println("for-each:");
        for (String city : cities) {
            System.out.println(city);
        }
        System.out.println("Iterator:");
        Iterator<String> iterator = cities.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        cities.add(0, "Воронеж");
        cities.add(cities.size() / 2, "Рязань");
        cities.add("Иркутск");
        cities.set(3, "Новосибирск");

        String target = "Сочи";
        System.out.println("Есть " + target + ": " + cities.contains(target));
        System.out.println("Индекс: " + cities.indexOf(target));

        if (!cities.isEmpty()) {
            cities.remove(1);
        }
        cities.remove("Уфа");
        System.out.println("Размер: " + cities.size());
        System.out.println("Пустой: " + cities.isEmpty());

        String[] cityArray = cities.toArray(new String[0]);
        System.out.println("Массив: " + Arrays.toString(cityArray));
    }
}
