package storage;

import model.*;
import service.Logger;
import service.ZooService;
import exception.IncompatibleAnimalsException;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

public class ZooFileStorage {
    private final String path;
    public ZooFileStorage(String path) { this.path = Entity.text(path); }
    public boolean exists() { return new File(path).exists(); }

    private String escape(String text) {
        return text.replace("\\", "\\\\").replace("|", "\\p")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
    private String unescape(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\') {
                if (++i == text.length()) throw new IllegalArgumentException("Оборванное экранирование");
                c = text.charAt(i);
                if (c == 'p') c = '|';
                else if (c == 'n') c = '\n';
                else if (c == 'r') c = '\r';
                else if (c != '\\') throw new IllegalArgumentException("Неизвестное экранирование");
            }
            result.append(c);
        }
        return result.toString();
    }
    private String[] split(String line) {
        String[] fields = line.split("\\|", -1);
        for (int i = 0; i < fields.length; i++) fields[i] = unescape(fields[i]);
        return fields;
    }
    private void row(BufferedWriter writer, String... fields) throws IOException {
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) writer.write('|');
            writer.write(escape(fields[i]));
        }
        writer.newLine();
    }
    public void save(Zoo zoo) throws IOException {
        File file = new File(path);
        File temp = new File(path + ".tmp");
        File backup = new File(path + ".bak");
        try (BufferedWriter out = new BufferedWriter(new FileWriter(temp))) {
            row(out, "ZOO", "1", zoo.getName());
            for (int i = 0; i < zoo.getKeepers().size(); i++) {
                Keeper k = (Keeper) zoo.getKeepers().get(i);
                row(out, "KEEPER", "" + k.getId(), k.getName());
            }
            for (int i = 0; i < zoo.getEnclosures().size(); i++) {
                Enclosure e = (Enclosure) zoo.getEnclosures().get(i);
                row(out, "ENCLOSURE", "" + e.getId(), e.getName(), e.getClimate().name(),
                        e.getHabitat().name(), "" + e.getCapacity(), "" + e.getKeeperId());
            }
            for (int i = 0; i < zoo.getAnimals().size(); i++) {
                Animal a = (Animal) zoo.getAnimals().get(i);
                row(out, "ANIMAL", a.getKind(), "" + a.getId(), a.getName(), a.getSpecies(),
                        "" + a.getBirthDate(), "" + a.getWeight(), a.getDiet().name(),
                        a.getClimate().name(), a.getFood().name(), "" + a.getEnclosureId());
            }
            for (int i = 0; i < zoo.getPlans().size(); i++) {
                FeedingPlan p = (FeedingPlan) zoo.getPlans().get(i);
                row(out, "PLAN", "" + p.getId(), "" + p.getAnimalId(), "" + p.getHour(),
                        "" + p.getMinute(), p.getLastFedDate() == null ? "-" : "" + p.getLastFedDate());
            }
            for (int i = 0; i < zoo.getVets().size(); i++) {
                VetRecord v = (VetRecord) zoo.getVets().get(i);
                row(out, "VET", "" + v.getId(), "" + v.getAnimalId(), "" + v.getDate(),
                        v.getDiagnosis(), "" + v.isSick());
            }
            for (int i = 0; i < zoo.getFeedings().size(); i++) {
                FeedingEvent e = (FeedingEvent) zoo.getFeedings().get(i);
                row(out, "FEED", "" + e.getId(), "" + e.getPlanId(), "" + e.getAnimalId(),
                        "" + e.getTime(), e.getFood().name(), "" + e.getKilograms(), "" + e.getCostKopecks());
            }
            for (int i = 0; i < zoo.getVisitors().size(); i++) {
                Visitor v = (Visitor) zoo.getVisitors().get(i);
                row(out, "VISITOR", "" + v.getId(), v.getName(), "" + v.getDate(),
                        v.getTicket().name(), "" + v.getPaidKopecks());
            }
            for (FoodType food : FoodType.values())
                row(out, "STOCK", food.name(), "" + zoo.getInventory().get(food));
        }
        if (file.exists()) {
            if (backup.exists() && !backup.delete()) throw new IOException("Не удалить старую копию");
            if (!file.renameTo(backup)) throw new IOException("Не создать резервную копию");
        }
        if (!temp.renameTo(file)) {
            if (!file.exists() && backup.exists() && !backup.renameTo(file))
                throw new IOException("Не сохранить файл. Предыдущие данные: " + backup.getPath());
            throw new IOException("Не заменить файл данных");
        }
    }
    private int phase(String type) {
        switch (type) {
            case "KEEPER": return 0;
            case "ENCLOSURE": return 1;
            case "ANIMAL": return 2;
            case "PLAN": return 3;
            case "VET": case "VISITOR": case "STOCK": return 4;
            case "FEED": return 5;
            default: return -1;
        }
    }
    private void expect(String[] fields, int count) {
        if (fields.length != count) throw new IllegalArgumentException("Неверное число полей");
    }
    private int integer(String text) { return Integer.parseInt(text); }
    private boolean bool(String text) {
        if (!text.equals("true") && !text.equals("false"))
            throw new IllegalArgumentException("Неверное логическое значение");
        return text.equals("true");
    }
    private void loadRow(String[] f, ZooService service) throws IncompatibleAnimalsException {
        Zoo zoo = service.getZoo();
        switch (f[0]) {
            case "KEEPER":
                expect(f, 3);
                service.addKeeper(new Keeper(integer(f[1]), f[2]));
                break;
            case "ENCLOSURE":
                expect(f, 7);
                Enclosure enclosure = new Enclosure(integer(f[1]), f[2], Climate.valueOf(f[3]),
                        Habitat.valueOf(f[4]), integer(f[5]));
                enclosure.setKeeperId(integer(f[6]));
                service.addEnclosure(enclosure);
                break;
            case "ANIMAL":
                expect(f, 11);
                service.addAnimal(Animal.create(f[1], integer(f[2]), f[3], f[4], LocalDate.parse(f[5]),
                        Double.parseDouble(f[6]), Diet.valueOf(f[7]), Climate.valueOf(f[8]),
                        FoodType.valueOf(f[9])), integer(f[10]));
                break;
            case "PLAN":
                expect(f, 6);
                service.addPlan(new FeedingPlan(integer(f[1]), integer(f[2]), integer(f[3]),
                        integer(f[4]), f[5].equals("-") ? null : LocalDate.parse(f[5])));
                break;
            case "VET":
                expect(f, 6);
                service.addExam(new VetRecord(integer(f[1]), integer(f[2]), LocalDate.parse(f[3]),
                        f[4], bool(f[5])));
                break;
            case "VISITOR":
                expect(f, 6);
                zoo.getVisitors().add(new Visitor(integer(f[1]), f[2], LocalDate.parse(f[3]),
                        TicketType.valueOf(f[4]), Long.parseLong(f[5])));
                break;
            case "STOCK":
                expect(f, 3);
                zoo.getInventory().set(FoodType.valueOf(f[1]), Double.parseDouble(f[2]));
                break;
            case "FEED":
                expect(f, 8);
                service.restoreFeeding(new FeedingEvent(integer(f[1]), integer(f[2]), integer(f[3]),
                        LocalDateTime.parse(f[4]), FoodType.valueOf(f[5]), Double.parseDouble(f[6]),
                        Long.parseLong(f[7])));
                break;
            default: throw new IllegalArgumentException("Неизвестная запись");
        }
    }
    private void warning(Logger logger, int line, String message) {
        String text = "Пропущена строка " + line + ": " + message;
        System.out.println(text);
        logger.log(text);
    }
    public Zoo load(Logger logger) throws IOException {
        String[][] rows = new String[32][];
        int[] numbers = new int[32];
        int size = 0;
        Zoo zoo;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            try {
                String header = reader.readLine();
                if (header == null) throw new IllegalArgumentException("Файл пуст");
                String[] f = split(header);
                expect(f, 3);
                if (!f[0].equals("ZOO") || !f[1].equals("1"))
                    throw new IllegalArgumentException("Неизвестный формат файла");
                zoo = new Zoo(f[2]);
            } catch (RuntimeException e) {
                throw new IOException("Повреждён заголовок: " + e.getMessage(), e);
            }
            String line;
            int number = 1;
            while ((line = reader.readLine()) != null) {
                number++;
                if (line.trim().isEmpty()) continue;
                try {
                    String[] f = split(line);
                    if (phase(f[0]) < 0) throw new IllegalArgumentException("Неизвестный тип записи");
                    if (size == rows.length) {
                        rows = Arrays.copyOf(rows, rows.length * 2);
                        numbers = Arrays.copyOf(numbers, numbers.length * 2);
                    }
                    rows[size] = f;
                    numbers[size++] = number;
                } catch (RuntimeException e) { warning(logger, number, e.getMessage()); }
            }
        }
        ZooService service = new ZooService(zoo, logger);
        for (int pass = 0; pass <= 5; pass++) for (int i = 0; i < size; i++) {
            if (phase(rows[i][0]) != pass) continue;
            try { loadRow(rows[i], service); }
            catch (IncompatibleAnimalsException e) { warning(logger, numbers[i], e.getMessage()); }
            catch (RuntimeException e) { warning(logger, numbers[i], e.getMessage()); }
        }
        logger.log("Данные загружены из " + path);
        return zoo;
    }
}
