package model;
import java.time.LocalDate;

public class VetRecord extends Entity {
    private final int animalId;
    private final LocalDate date;
    private final String diagnosis;
    private final boolean sick;

    public VetRecord(int id, int animalId, LocalDate date, String diagnosis, boolean sick) {
        super(id);
        if (animalId <= 0 || date == null || date.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Некорректные данные осмотра");
        this.animalId = animalId;
        this.date = date;
        this.diagnosis = text(diagnosis);
        this.sick = sick;
    }
    public int getAnimalId() { return animalId; }
    public LocalDate getDate() { return date; }
    public String getDiagnosis() { return diagnosis; }
    public boolean isSick() { return sick; }
    @Override public String toString() {
        return String.format("%4d животное:%d %s %-8s %s", getId(), animalId,
                date, sick ? "Болеет" : "Здоров", diagnosis);
    }
}
