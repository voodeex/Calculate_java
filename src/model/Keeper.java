package model;

public class Keeper extends Entity {
    private final String name;
    public Keeper(int id, String name) { super(id); this.name = text(name); }
    public String getName() { return name; }
    @Override public String toString() { return String.format("%4d %s", getId(), name); }
}
