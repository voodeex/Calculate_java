package model;

public abstract class Entity {
    private final int id;

    protected Entity(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID должен быть положительным");
        this.id = id;
    }

    public final int getId() { return id; }

    public static String text(String value) {
        if (value == null || value.trim().isEmpty() || value.trim().length() > 100)
            throw new IllegalArgumentException("Текст должен содержать от 1 до 100 символов");
        return value.trim();
    }

    public static double number(double value, double min, double max) {
        if (Double.isNaN(value) || Double.isInfinite(value) || value < min || value > max)
            throw new IllegalArgumentException("Число вне допустимого диапазона");
        return value;
    }

    @Override public final boolean equals(Object other) {
        return other != null && getClass() == other.getClass()
                && id == ((Entity) other).id;
    }

    @Override public final int hashCode() { return 31 * getClass().hashCode() + id; }
}
