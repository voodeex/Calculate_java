import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Zoo zoo = new Zoo();
        seed(zoo);
        runMenu(zoo);
    }

    private static void seed(Zoo zoo) {
        try {
            zoo.addEnclosure(new Enclosure(1, "Саванна", 4));
            zoo.addEnclosure(new Enclosure(2, "Террариум", 3));
            zoo.addEnclosure(new Enclosure(3, "Птичник", 5));
            zoo.addAnimal(new Mammal(1, "Лев Боня", 6, 190, Diet.CARNIVORE));
            zoo.addAnimal(new Mammal(2, "Зебра Зоя", 4, 310, Diet.HERBIVORE));
            zoo.addAnimal(new Bird(3, "Попугай Кеша", 2, 0.8, Diet.HERBIVORE));
            zoo.addAnimal(new Reptile(4, "Игуана Ира", 3, 4.2, Diet.HERBIVORE));
            zoo.moveAnimal(1, 1);
            zoo.moveAnimal(3, 3);
            zoo.moveAnimal(4, 2);
        } catch (ZooException exception) {
            throw new IllegalStateException("Ошибка демонстрационных данных", exception);
        }
    }

    private static void runMenu(Zoo zoo) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            printMenu();
            Integer command = readInt(scanner, "Команда: ");
            if (command == null) {
                System.out.println("Ввод завершён.");
                break;
            }
            try {
                switch (command) {
                    case 1:
                        zoo.showAnimals();
                        break;
                    case 2:
                        addAnimal(scanner, zoo);
                        break;
                    case 3:
                        renameAnimal(scanner, zoo);
                        break;
                    case 4:
                        deleteAnimal(scanner, zoo);
                        break;
                    case 5:
                        moveAnimal(scanner, zoo);
                        break;
                    case 6:
                        zoo.showEnclosures();
                        break;
                    case 7:
                        zoo.feedAll();
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Нет такой команды.");
                }
            } catch (ZooException | IllegalArgumentException exception) {
                System.out.println("Ошибка: " + exception.getMessage());
            }
        }
    }

    private static void addAnimal(Scanner scanner, Zoo zoo) throws ZooException {
        Integer id = readRequiredInt(scanner, "ID: ");
        String name = readLine(scanner, "Имя: ");
        Integer age = readRequiredInt(scanner, "Возраст: ");
        Double weight = readRequiredDouble(scanner, "Вес: ");
        Integer type = readRequiredInt(scanner, "Тип (1-млекопитающее, 2-птица, 3-рептилия): ");
        Integer dietNumber = readRequiredInt(scanner, "Рацион (1-травоядное, 2-хищник, 3-всеядное): ");
        Diet diet = dietNumber == 1 ? Diet.HERBIVORE
                : dietNumber == 2 ? Diet.CARNIVORE : Diet.OMNIVORE;
        Animal animal;
        if (type == 1) {
            animal = new Mammal(id, name, age, weight, diet);
        } else if (type == 2) {
            animal = new Bird(id, name, age, weight, diet);
        } else if (type == 3) {
            animal = new Reptile(id, name, age, weight, diet);
        } else {
            throw new ZooException("Неизвестный тип животного");
        }
        zoo.addAnimal(animal);
        System.out.println("Животное добавлено.");
    }

    private static void renameAnimal(Scanner scanner, Zoo zoo) throws ZooException {
        int id = readRequiredInt(scanner, "ID животного: ");
        zoo.renameAnimal(id, readLine(scanner, "Новое имя: "));
        System.out.println("Имя изменено.");
    }

    private static void deleteAnimal(Scanner scanner, Zoo zoo) throws ZooException {
        zoo.deleteAnimal(readRequiredInt(scanner, "ID животного: "));
        System.out.println("Животное удалено.");
    }

    private static void moveAnimal(Scanner scanner, Zoo zoo) throws ZooException {
        int animalId = readRequiredInt(scanner, "ID животного: ");
        int enclosureId = readRequiredInt(scanner, "ID вольера: ");
        zoo.moveAnimal(animalId, enclosureId);
        System.out.println("Животное перемещено.");
    }

    private static Integer readRequiredInt(Scanner scanner, String prompt) throws ZooException {
        Integer value = readInt(scanner, prompt);
        if (value == null) {
            throw new ZooException("Ввод завершён");
        }
        return value;
    }

    private static Integer readInt(Scanner scanner, String prompt) {
        while (true) {
            String value = readLine(scanner, prompt);
            if (value == null) {
                return null;
            }
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Введите целое число.");
            }
        }
    }

    private static Double readRequiredDouble(Scanner scanner, String prompt) throws ZooException {
        while (true) {
            String value = readLine(scanner, prompt);
            if (value == null) {
                throw new ZooException("Ввод завершён");
            }
            try {
                return Double.parseDouble(value.replace(',', '.'));
            } catch (NumberFormatException exception) {
                System.out.println("Введите число.");
            }
        }
    }

    private static String readLine(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }

    private static void printMenu() {
        System.out.println("\n=== Зоопарк ===");
        System.out.println("1. Показать животных");
        System.out.println("2. Добавить животное");
        System.out.println("3. Изменить имя");
        System.out.println("4. Удалить животное");
        System.out.println("5. Переместить в вольер");
        System.out.println("6. Показать вольеры");
        System.out.println("7. Кормление и суточный рацион");
        System.out.println("0. Выход");
    }
}
