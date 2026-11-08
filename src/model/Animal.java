package model;

import java.time.LocalDate;

public abstract class Animal extends Entity {
    private String name;
    private final String species;
    private final LocalDate birthDate;
    private double weight;
    private final Diet diet;
    private final Climate climate;
    private final FoodType food;
    private int enclosureId;

    protected Animal(int id, String name, String species, LocalDate birthDate,
                     double weight, Diet diet, Climate climate, FoodType food) {
        super(id);
        setName(name);
        this.species = text(species);
        if (birthDate == null || birthDate.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Некорректная дата рождения");
        if (diet == null || climate == null || !diet.accepts(food))
            throw new IllegalArgumentException("Рацион, климат или корм некорректны");
        this.birthDate = birthDate;
        this.diet = diet;
        this.climate = climate;
        this.food = food;
        setWeight(weight);
    }

    public String getName() { return name; }
    public String getSpecies() { return species; }
    public LocalDate getBirthDate() { return birthDate; }
    public double getWeight() { return weight; }
    public Diet getDiet() { return diet; }
    public Climate getClimate() { return climate; }
    public FoodType getFood() { return food; }
    public int getEnclosureId() { return enclosureId; }
    public void setName(String name) { this.name = text(name); }
    public void setWeight(double weight) { this.weight = number(weight, 0.01, 100000); }
    public void setEnclosureId(int id) {
        if (id < 0) throw new IllegalArgumentException("Некорректный ID вольера");
        enclosureId = id;
    }

    protected double ration(double coefficient) {
        return Math.max(0.001, Math.round(weight * coefficient * 1000) / 1000.0);
    }

    public abstract double dailyFoodKg();
    public abstract Habitat getHabitat();
    public abstract String getKind();

    public static Animal create(String kind, int id, String name, String species,
                                LocalDate birth, double weight, Diet diet,
                                Climate climate, FoodType food) {
        switch (kind) {
            case "MAMMAL": return new Mammal(id, name, species, birth, weight, diet, climate, food);
            case "BIRD": return new Bird(id, name, species, birth, weight, diet, climate, food);
            case "REPTILE": return new Reptile(id, name, species, birth, weight, diet, climate, food);
            case "FISH": return new Fish(id, name, species, birth, weight, diet, climate, food);
            case "FLYING_BIRD": return new FlyingBird(id, name, species, birth, weight, diet, climate, food);
            case "FLYING_MAMMAL": return new FlyingMammal(id, name, species, birth, weight, diet, climate, food);
            case "AQUATIC_MAMMAL": return new AquaticMammal(id, name, species, birth, weight, diet, climate, food);
            default: throw new IllegalArgumentException("Неизвестный тип животного: " + kind);
        }
    }

    @Override public String toString() {
        return String.format("%4d %-16.16s %-18.18s %9.3f %-12.12s %5d",
                getId(), name, species, weight, diet, enclosureId);
    }
}
