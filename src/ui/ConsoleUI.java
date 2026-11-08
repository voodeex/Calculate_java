package ui;

import model.*;
import service.*;
import storage.EntityStorage;
import storage.ZooFileStorage;
import exception.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ConsoleUI {
    private final ZooService service;
    private final ZooFileStorage files;
    private final Logger logger;
    private final Input input = new Input();
    private final Reports reports;

    public ConsoleUI(ZooService service, ZooFileStorage files, Logger logger) {
        this.service = service;
        this.files = files;
        this.logger = logger;
        reports = new Reports(service);
    }
    private Zoo zoo() { return service.getZoo(); }
    private int id(String prompt) { return input.integer(prompt, 1, Integer.MAX_VALUE); }
    private void show(EntityStorage storage) {
        if (storage.size() == 0) System.out.println("Нет записей");
        for (int i = 0; i < storage.size(); i++) System.out.println(storage.get(i));
    }
    private void save() {
        try {
            files.save(zoo());
            logger.log("Данные сохранены");
            System.out.println("Данные сохранены в zoo.txt");
        } catch (IOException e) {
            logger.log("Ошибка сохранения: " + e.getMessage());
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
    }
    public void run() {
        System.out.println(zoo());
        boolean running = true;
        try {
            while (running) {
                System.out.println("\n1. Животные и вольеры\n2. Смотрители\n3. Кормление"
                        + "\n4. Ветеринария\n5. Билеты\n6. Отчёты\n7. Сохранить\n8. Журнал\n0. Выход");
                int command = input.integer("> ", 0, 8);
                if (command == 0) running = false;
                else if (command == 7) save();
                else if (command == 8) for (String entry : logger.getEntries()) System.out.println(entry);
                else section(command);
            }
        } catch (Input.Closed e) {
            System.out.println("\nВвод завершён");
        }
        save();
    }
    private void section(int section) {
        while (true) {
            if (section == 1) System.out.println("\n1. Список животных\n2. Добавить животное"
                    + "\n3. Вольеры\n4. Добавить вольер\n5. Переместить животное\n6. Способности\n0. Назад");
            if (section == 2) System.out.println("\n1. Смотрители\n2. Добавить\n3. Закрепить за вольером\n0. Назад");
            if (section == 3) System.out.println("\n1. Планы\n2. Назначить или изменить время"
                    + "\n3. Склад\n4. Пополнить склад\n5. Кормить по расписанию\n6. История кормлений\n0. Назад");
            if (section == 4) System.out.println("\n1. Записать осмотр\n2. История осмотров\n3. Больные\n0. Назад");
            if (section == 5) System.out.println("\n1. Продать билет\n2. Проданные билеты\n0. Назад");
            if (section == 6) System.out.println("\n1. Затраты на корм\n2. Загрузка вольеров"
                    + "\n3. Больные животные\n4. Выручка\n0. Назад");
            int max = section == 1 || section == 3 ? 6 : section == 6 ? 4 : section == 5 ? 2 : 3;
            int command = input.integer("> ", 0, max);
            if (command == 0) return;
            try { execute(section, command); }
            catch (IncompatibleAnimalsException e) { error(e.getMessage()); }
            catch (FoodShortageException e) { error("Недостаточно корма. " + e.getMessage()); }
            catch (ZooOperationException e) { error(e.getMessage()); }
            catch (IllegalArgumentException e) { error("Неверные данные: " + e.getMessage()); }
            catch (RuntimeException e) {
                if (e instanceof Input.Closed) throw e;
                error("Операция не выполнена: " + e.getMessage());
            }
        }
    }
    private void error(String message) {
        System.out.println(message);
        logger.log("Отклонена операция: " + message);
    }
    private void execute(int section, int command) throws IncompatibleAnimalsException {
        switch (section * 10 + command) {
            case 11:
                Animal[] animals = service.animals();
                int order = input.integer("Сортировка: 1 — имя, 2 — вес: ", 1, 2);
                AnimalComparator comparator;
                if (order == 1) comparator = new AnimalSorter.ByName();
                else comparator = new AnimalSorter.ByWeight();
                AnimalSorter.sort(animals, comparator);
                System.out.printf("%4s %-16s %-18s %9s %-12s %5s%n", "ID", "Имя", "Вид", "Вес, кг", "Рацион", "Вольер");
                for (Animal animal : animals) System.out.println(animal);
                break;
            case 12: addAnimal(); break;
            case 13: show(zoo().getEnclosures()); break;
            case 14:
                String name = input.text("Название: ");
                Climate climate = (Climate) input.choose("Климат:", Climate.values());
                Habitat habitat = (Habitat) input.choose("Тип:", Habitat.values());
                int capacity = input.integer("Вместимость: ", 1, 10000);
                service.addEnclosure(new Enclosure(zoo().getEnclosures().nextId(), name, climate, habitat, capacity));
                break;
            case 15:
                service.move(id("ID животного: "), id("ID нового вольера: "));
                System.out.println("Животное перемещено");
                break;
            case 16:
                Animal animal = service.animal(id("ID животного: "));
                if (animal instanceof Flyable) System.out.println(((Flyable) animal).fly());
                if (animal instanceof Swimmable) System.out.println(((Swimmable) animal).swim());
                System.out.printf("Суточная норма: %.3f кг, корм: %s%n", animal.dailyFoodKg(), animal.getFood());
                break;
            case 21: show(zoo().getKeepers()); break;
            case 22:
                service.addKeeper(new Keeper(zoo().getKeepers().nextId(), input.text("ФИО: ")));
                break;
            case 23: service.assignKeeper(id("ID вольера: "), id("ID смотрителя: ")); break;
            case 31: show(zoo().getPlans()); break;
            case 32:
                service.schedule(id("ID животного: "), input.integer("Час: ", 0, 23),
                        input.integer("Минуты: ", 0, 59));
                break;
            case 33: System.out.print(zoo().getInventory()); break;
            case 34:
                FoodType food = (FoodType) input.choose("Корм:", FoodType.values());
                service.restock(food, input.decimal("Количество, кг: ", 0.001, 1000000));
                break;
            case 35: System.out.println("Накормлено: " + service.feedDue(LocalDateTime.now())); break;
            case 36: show(zoo().getFeedings()); break;
            case 41:
                int animalId = id("ID животного: ");
                LocalDate date = input.date("Дата осмотра ГГГГ-ММ-ДД: ");
                String diagnosis = input.text("Диагноз / заключение: ");
                boolean sick = input.integer("Болеет? 1 — да, 0 — нет: ", 0, 1) == 1;
                service.addExam(new VetRecord(zoo().getVets().nextId(), animalId, date, diagnosis, sick));
                break;
            case 42: show(zoo().getVets()); break;
            case 43: case 63: reports.sickAnimals(); break;
            case 51:
                String visitor = input.text("Имя посетителя: ");
                TicketType ticket = (TicketType) input.choose("Тип билета:", TicketType.values());
                System.out.println(service.sellTicket(visitor, ticket));
                break;
            case 52: show(zoo().getVisitors()); break;
            case 61: reports.foodCosts(); break;
            case 62: reports.occupancy(); break;
            case 64: reports.tickets(); break;
            default: throw new ZooOperationException("Неизвестная команда");
        }
    }
    private void addAnimal() throws IncompatibleAnimalsException {
        String[] kinds = {"MAMMAL", "BIRD", "REPTILE", "FISH", "FLYING_BIRD", "FLYING_MAMMAL", "AQUATIC_MAMMAL"};
        String[] titles = {"Млекопитающее", "Нелетающая птица", "Рептилия", "Рыба",
                "Летающая птица", "Летающее млекопитающее", "Водное млекопитающее"};
        for (int i = 0; i < titles.length; i++) System.out.println((i + 1) + ". " + titles[i]);
        String kind = kinds[input.integer("Тип: ", 1, titles.length) - 1];
        String name = input.text("Имя: ");
        String species = input.text("Вид: ");
        LocalDate birth = input.date("Дата рождения ГГГГ-ММ-ДД: ");
        double weight = input.decimal("Вес, кг: ", 0.01, 100000);
        Diet diet = (Diet) input.choose("Рацион:", Diet.values());
        Climate climate = (Climate) input.choose("Необходимый климат:", Climate.values());
        FoodType food = (FoodType) input.choose("Корм:", FoodType.values());
        Animal animal = Animal.create(kind, zoo().getAnimals().nextId(), name, species,
                birth, weight, diet, climate, food);
        service.addAnimal(animal, id("ID вольера: "));
        System.out.println("Добавлено животное " + animal.getId());
    }
}
