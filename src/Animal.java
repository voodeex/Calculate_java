public abstract class Animal {
    private final int id;
    private String name;
    private final int age;
    private final double weightKg;
    private final Diet diet;
    private int enclosureId;

    protected Animal(int id, String name, int age, double weightKg, Diet diet) {
        if (id <= 0 || name == null || name.trim().isEmpty() || age < 0 || weightKg <= 0) {
            throw new IllegalArgumentException("Некорректные данные животного");
        }
        this.id = id;
        this.name = name.trim();
        this.age = age;
        this.weightKg = weightKg;
        this.diet = diet;
        this.enclosureId = -1;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        this.name = name.trim();
    }

    public int getAge() {
        return age;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public Diet getDiet() {
        return diet;
    }

    public int getEnclosureId() {
        return enclosureId;
    }

    public void setEnclosureId(int enclosureId) {
        this.enclosureId = enclosureId;
    }

    public abstract String getType();

    public abstract String makeSound();

    public abstract double dailyFoodKg();

    @Override
    public String toString() {
        return String.format("%s{id=%d, имя='%s', возраст=%d, вес=%.1f кг, рацион=%s, вольер=%s}",
                getType(), id, name, age, weightKg, diet.getTitle(),
                enclosureId < 0 ? "не назначен" : String.valueOf(enclosureId));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Animal)) {
            return false;
        }
        return id == ((Animal) object).id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
