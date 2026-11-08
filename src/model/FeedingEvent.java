package model;
import java.time.LocalDateTime;

public class FeedingEvent extends Entity {
    private final int planId;
    private final int animalId;
    private final LocalDateTime time;
    private final FoodType food;
    private final double kilograms;
    private final long costKopecks;

    public FeedingEvent(int id, int planId, int animalId, LocalDateTime time,
                        FoodType food, double kilograms, long costKopecks) {
        super(id);
        if (planId <= 0 || animalId <= 0 || time == null || time.isAfter(LocalDateTime.now())
                || food == null || costKopecks < 0 || costKopecks > 1_000_000_000_000_000L)
            throw new IllegalArgumentException("Некорректные данные кормления");
        this.planId = planId;
        this.animalId = animalId;
        this.time = time;
        this.food = food;
        this.kilograms = number(kilograms, 0.001, 100000);
        this.costKopecks = costKopecks;
    }
    public int getPlanId() { return planId; }
    public int getAnimalId() { return animalId; }
    public LocalDateTime getTime() { return time; }
    public FoodType getFood() { return food; }
    public double getKilograms() { return kilograms; }
    public long getCostKopecks() { return costKopecks; }
    @Override public String toString() {
        return String.format("%s животное:%d %s %.3f кг, %.2f руб.",
                time, animalId, food, kilograms, costKopecks / 100.0);
    }
}
