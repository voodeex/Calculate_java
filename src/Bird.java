public class Bird extends Animal {
    public Bird(int id, String name, int age, double weightKg, Diet diet) {
        super(id, name, age, weightKg, diet);
    }

    @Override
    public String getType() {
        return "Птица";
    }

    @Override
    public String makeSound() {
        return getName() + " щебечет";
    }

    @Override
    public double dailyFoodKg() {
        return getWeightKg() * 0.08;
    }
}
