public class Mammal extends Animal {
    public Mammal(int id, String name, int age, double weightKg, Diet diet) {
        super(id, name, age, weightKg, diet);
    }

    @Override
    public String getType() {
        return "Млекопитающее";
    }

    @Override
    public String makeSound() {
        return getName() + " рычит";
    }

    @Override
    public double dailyFoodKg() {
        return getWeightKg() * 0.05;
    }
}
