package model;

public enum Diet {
    HERBIVORE("Травоядное"), CARNIVORE("Хищник"), OMNIVORE("Всеядное");

    private final String title;
    Diet(String title) { this.title = title; }

    public boolean compatible(Diet other) {
        return !((this == CARNIVORE && other == HERBIVORE)
                || (this == HERBIVORE && other == CARNIVORE));
    }

    public boolean accepts(FoodType food) {
        return food != null && (this == OMNIVORE
                || (this == HERBIVORE && food.isPlant())
                || (this == CARNIVORE && !food.isPlant()));
    }

    @Override public String toString() { return title; }
}
