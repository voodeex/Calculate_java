package model;
import java.time.LocalDate;

public class FlyingBird extends Bird implements Flyable {
    public FlyingBird(int id, String name, String species, LocalDate birth, double weight,
                      Diet diet, Climate climate, FoodType food) {
        super(id, name, species, birth, weight, diet, climate, food);
    }
    @Override public String getKind() { return "FLYING_BIRD"; }
    @Override public String fly() { return getName() + " летит с помощью перьев и крыльев"; }
}
