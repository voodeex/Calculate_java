package model;
import java.time.LocalDate;

public class Reptile extends Animal {
    public Reptile(int id, String name, String species, LocalDate birth, double weight,
                   Diet diet, Climate climate, FoodType food) {
        super(id, name, species, birth, weight, diet, climate, food);
    }
    @Override public double dailyFoodKg() { return ration(0.01); }
    @Override public Habitat getHabitat() { return Habitat.LAND; }
    @Override public String getKind() { return "REPTILE"; }
}
