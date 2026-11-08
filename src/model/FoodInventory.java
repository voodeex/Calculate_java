package model;
import exception.FoodShortageException;

public class FoodInventory {
    private final double[] quantities = new double[FoodType.values().length];

    public double get(FoodType food) {
        if (food == null) throw new IllegalArgumentException("Корм не выбран");
        return quantities[food.ordinal()];
    }
    public void set(FoodType food, double kilograms) {
        get(food);
        quantities[food.ordinal()] = Entity.number(kilograms, 0, 1_000_000_000);
    }
    public void add(FoodType food, double kilograms) {
        Entity.number(kilograms, 0.001, 1_000_000);
        set(food, get(food) + kilograms);
    }
    public void require(FoodType food, double kilograms) {
        Entity.number(kilograms, 0, 1_000_000_000);
        if (get(food) + 0.000000001 < kilograms)
            throw new FoodShortageException(String.format(
                    "%s: требуется %.3f кг, на складе %.3f кг", food, kilograms, get(food)));
    }
    public void consume(FoodType food, double kilograms) {
        require(food, kilograms);
        set(food, Math.max(0, get(food) - kilograms));
    }
    @Override public String toString() {
        StringBuilder result = new StringBuilder();
        for (FoodType food : FoodType.values())
            result.append(String.format("%-20s %10.3f кг%n", food, get(food)));
        return result.toString();
    }
}
