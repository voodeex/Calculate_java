package service;
import model.*;
import exception.IncompatibleAnimalsException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class DemoData {
    private DemoData() { }
    public static Zoo create(Logger logger) throws IncompatibleAnimalsException {
        Zoo zoo = new Zoo("Городской зоопарк");
        ZooService service = new ZooService(zoo, logger);
        service.addKeeper(new Keeper(1, "Иван Петров"));
        service.addKeeper(new Keeper(2, "Анна Смирнова"));
        service.addKeeper(new Keeper(3, "Олег Соколов"));
        String[] names = {"Хищники", "Саванна", "Лес", "Тропические птицы",
                "Рептилии", "Тропические рыбы", "Летучие мыши", "Холодный бассейн"};
        Climate[] climates = {Climate.TROPICAL, Climate.TROPICAL, Climate.TEMPERATE,
                Climate.TROPICAL, Climate.TROPICAL, Climate.TROPICAL, Climate.TROPICAL, Climate.COLD};
        Habitat[] habitats = {Habitat.LAND, Habitat.LAND, Habitat.LAND, Habitat.AVIARY,
                Habitat.LAND, Habitat.WATER, Habitat.AVIARY, Habitat.WATER};
        for (int i = 0; i < names.length; i++) {
            service.addEnclosure(new Enclosure(i + 1, names[i], climates[i], habitats[i], 5));
            service.assignKeeper(i + 1, i % 3 + 1);
        }
        String[][] animals = {
            {"MAMMAL", "Симба", "Лев", "180", "CARNIVORE", "MEAT", "1"},
            {"MAMMAL", "Шерхан", "Тигр", "160", "CARNIVORE", "MEAT", "1"},
            {"MAMMAL", "Дамбо", "Слон", "4000", "HERBIVORE", "PLANTS", "2"},
            {"MAMMAL", "Мартин", "Зебра", "280", "HERBIVORE", "PLANTS", "2"},
            {"MAMMAL", "Мелман", "Жираф", "800", "HERBIVORE", "PLANTS", "2"},
            {"MAMMAL", "Бэмби", "Олень", "100", "HERBIVORE", "PLANTS", "3"},
            {"MAMMAL", "Борис", "Зубр", "650", "HERBIVORE", "PLANTS", "3"},
            {"FLYING_BIRD", "Кеша", "Попугай", "1.2", "OMNIVORE", "GRAIN", "4"},
            {"FLYING_BIRD", "Туки", "Тукан", "0.7", "OMNIVORE", "PLANTS", "4"},
            {"REPTILE", "Гена", "Крокодил", "150", "CARNIVORE", "MEAT", "5"},
            {"REPTILE", "Каа", "Питон", "35", "CARNIVORE", "MEAT", "5"},
            {"FISH", "Пира", "Пиранья", "1.5", "CARNIVORE", "FISH", "6"},
            {"FISH", "Ари", "Арована", "4", "CARNIVORE", "FISH", "6"},
            {"FLYING_MAMMAL", "Ночка", "Летучая мышь", "0.04", "CARNIVORE", "INSECTS", "7"},
            {"AQUATIC_MAMMAL", "Умка", "Тюлень", "120", "CARNIVORE", "FISH", "8"},
            {"AQUATIC_MAMMAL", "Норд", "Косатка", "2200", "CARNIVORE", "FISH", "8"}
        };
        for (int i = 0; i < animals.length; i++) {
            String[] a = animals[i];
            int enclosureId = Integer.parseInt(a[6]);
            service.addAnimal(Animal.create(a[0], i + 1, a[1], a[2], LocalDate.now().minusYears(3),
                    Double.parseDouble(a[3]), Diet.valueOf(a[4]), climates[enclosureId - 1],
                    FoodType.valueOf(a[5])), enclosureId);
            service.schedule(i + 1, 8, 0);
        }
        for (FoodType food : FoodType.values()) service.restock(food, 1000);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        service.feedDue(LocalDateTime.of(yesterday, java.time.LocalTime.of(8, 0)));
        service.addExam(new VetRecord(1, 1, yesterday, "Воспаление лапы; назначено лечение", true));
        service.addExam(new VetRecord(2, 8, yesterday, "Плановый осмотр, здоров", false));
        service.addExam(new VetRecord(3, 12, yesterday, "Паразитарное заболевание", true));
        service.sellTicket("Демо: Алексей", TicketType.ADULT);
        service.sellTicket("Демо: Мария", TicketType.CHILD);
        logger.log("Созданы демонстрационные данные");
        return zoo;
    }
}
