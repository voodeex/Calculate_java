package model;

public enum FoodType {
    PLANTS("Растительный корм", 12000, true),
    GRAIN("Зерно", 8000, true),
    MEAT("Мясо", 40000, false),
    FISH("Рыба", 30000, false),
    INSECTS("Насекомые", 70000, false);

    private final String title;
    private final long priceKopecks;
    private final boolean plant;

    FoodType(String title, long priceKopecks, boolean plant) {
        this.title = title;
        this.priceKopecks = priceKopecks;
        this.plant = plant;
    }

    public long getPriceKopecks() { return priceKopecks; }
    public boolean isPlant() { return plant; }
    public long cost(double kilograms) {
        Entity.number(kilograms, 0, 1_000_000_000);
        return Math.round(kilograms * priceKopecks);
    }
    @Override public String toString() { return title; }
}
