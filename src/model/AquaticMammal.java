package model;
import java.time.LocalDate;

public class AquaticMammal extends Mammal implements Swimmable {
    public AquaticMammal(int id, String name, String species, LocalDate birth, double weight,
                         Diet diet, Climate climate, FoodType food) {
        super(id, name, species, birth, weight, diet, climate, food);
    }
    @Override public String getKind() { return "AQUATIC_MAMMAL"; }
    @Override public Habitat getHabitat() { return Habitat.WATER; }
    @Override public double dailyFoodKg() { return ration(0.06); }
    @Override public String swim() { return getName() + " плавает и всплывает для дыхания"; }
}
