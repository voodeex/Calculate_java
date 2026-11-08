package model;
import java.time.LocalDate;

public class Fish extends Animal implements Swimmable {
    public Fish(int id, String name, String species, LocalDate birth, double weight,
                Diet diet, Climate climate, FoodType food) {
        super(id, name, species, birth, weight, diet, climate, food);
    }
    @Override public double dailyFoodKg() { return ration(0.02); }
    @Override public Habitat getHabitat() { return Habitat.WATER; }
    @Override public String getKind() { return "FISH"; }
    @Override public String swim() { return getName() + " плавает в воде"; }
}
