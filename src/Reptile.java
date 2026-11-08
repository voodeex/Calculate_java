public class Reptile extends Animal {
    public Reptile(int id, String name, int age, double weightKg, Diet diet) {
        super(id, name, age, weightKg, diet);
    }

    @Override
    public String getType() {
        return "Рептилия";
    }

    @Override
    public String makeSound() {
        return getName() + " шипит";
    }

    @Override
    public double dailyFoodKg() {
        return getWeightKg() * 0.03;
    }
}
