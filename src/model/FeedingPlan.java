package model;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FeedingPlan extends Entity {
    private final int animalId;
    private int hour;
    private int minute;
    private LocalDate lastFedDate;

    public FeedingPlan(int id, int animalId, int hour, int minute, LocalDate lastFedDate) {
        super(id);
        if (animalId <= 0) throw new IllegalArgumentException("Некорректный ID животного");
        this.animalId = animalId;
        setTime(hour, minute);
        markFed(lastFedDate);
    }
    public int getAnimalId() { return animalId; }
    public int getHour() { return hour; }
    public int getMinute() { return minute; }
    public LocalDate getLastFedDate() { return lastFedDate; }
    public void setTime(int hour, int minute) {
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
            throw new IllegalArgumentException("Некорректное время кормления");
        this.hour = hour;
        this.minute = minute;
    }
    public void markFed(LocalDate date) {
        if (date != null && date.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Дата кормления находится в будущем");
        lastFedDate = date;
    }
    public boolean isDue(LocalDateTime now) {
        return (lastFedDate == null || lastFedDate.isBefore(now.toLocalDate()))
                && !now.toLocalTime().isBefore(java.time.LocalTime.of(hour, minute));
    }
    @Override public String toString() {
        return String.format("План %d: животное %d, %02d:%02d, последнее: %s",
                getId(), animalId, hour, minute, lastFedDate == null ? "нет" : lastFedDate);
    }
}
