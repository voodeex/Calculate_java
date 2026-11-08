package model;
import java.time.LocalDate;

public class Bird extends Animal {
    public Bird(int id, String name, String species, LocalDate birth, double weight,
                Diet diet, Climate climate, FoodType food) {
        super(id, name, species, birth, weight, diet, climate, food);
    }
    @Override public double dailyFoodKg() { return ration(0.08); }
    @Override public Habitat getHabitat() { return Habitat.AVIARY; }
    @Override public String getKind() { return "BIRD"; }
}
