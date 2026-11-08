package model;
import java.time.LocalDate;

public class FlyingMammal extends Mammal implements Flyable {
    public FlyingMammal(int id, String name, String species, LocalDate birth, double weight,
                        Diet diet, Climate climate, FoodType food) {
        super(id, name, species, birth, weight, diet, climate, food);
    }
    @Override public String getKind() { return "FLYING_MAMMAL"; }
    @Override public Habitat getHabitat() { return Habitat.AVIARY; }
    @Override public double dailyFoodKg() { return ration(0.3); }
    @Override public String fly() { return getName() + " летит с помощью перепончатых крыльев"; }
}
