package service;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Logger {
    private String[] entries = new String[16];
    private int size;
    private final String path;

    public Logger(String path) { this.path = model.Entity.text(path); }
    public void log(String message) {
        String entry = LocalDateTime.now() + " | "
                + message.replace('\n', ' ').replace('\r', ' ');
        if (size == entries.length) entries = Arrays.copyOf(entries, entries.length * 2);
        entries[size++] = entry;
        try (FileWriter writer = new FileWriter(path, true)) {
            writer.write(entry + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Не удалось записать журнал: " + e.getMessage());
        }
    }
    public String[] getEntries() { return Arrays.copyOf(entries, size); }
    @Override public String toString() { return "Записей за сеанс: " + size; }
}
