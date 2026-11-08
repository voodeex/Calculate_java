package model;

public class Enclosure extends Entity {
    private final String name;
    private final Climate climate;
    private final Habitat habitat;
    private final int capacity;
    private int keeperId;

    public Enclosure(int id, String name, Climate climate, Habitat habitat, int capacity) {
        super(id);
        this.name = text(name);
        if (climate == null || habitat == null || capacity < 1 || capacity > 10000)
            throw new IllegalArgumentException("Некорректные параметры вольера");
        this.climate = climate;
        this.habitat = habitat;
        this.capacity = capacity;
    }
    public String getName() { return name; }
    public Climate getClimate() { return climate; }
    public Habitat getHabitat() { return habitat; }
    public int getCapacity() { return capacity; }
    public int getKeeperId() { return keeperId; }
    public void setKeeperId(int id) {
        if (id < 0) throw new IllegalArgumentException("Некорректный ID смотрителя");
        keeperId = id;
    }
    @Override public String toString() {
        return String.format("%4d %-22.22s %-12.12s %-22.22s мест:%4d смотритель:%d",
                getId(), name, climate, habitat, capacity, keeperId);
    }
}
