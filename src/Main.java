import model.Zoo;
import service.DemoData;
import service.Logger;
import service.ZooService;
import storage.ZooFileStorage;
import ui.ConsoleUI;
import exception.IncompatibleAnimalsException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Logger logger = new Logger("log.txt");
        ZooFileStorage files = new ZooFileStorage("zoo.txt");
        try {
            Zoo zoo = files.exists() ? files.load(logger) : DemoData.create(logger);
            new ConsoleUI(new ZooService(zoo, logger), files, logger).run();
        } catch (IOException e) {
            System.out.println("Не удалось загрузить данные: " + e.getMessage());
            System.out.println("Проверьте zoo.txt. Исходный файл сохранён без изменений.");
            logger.log("Ошибка загрузки: " + e.getMessage());
        } catch (IncompatibleAnimalsException e) {
            System.out.println("Ошибка демонстрационных данных: " + e.getMessage());
            logger.log("Ошибка демонстрационных данных: " + e.getMessage());
        }
    }
}
