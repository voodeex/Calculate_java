package model;
import storage.EntityStorage;

public class Zoo {
    private final String name;
    private final EntityStorage animals = new EntityStorage();
    private final EntityStorage enclosures = new EntityStorage();
    private final EntityStorage keepers = new EntityStorage();
    private final EntityStorage plans = new EntityStorage();
    private final EntityStorage vets = new EntityStorage();
    private final EntityStorage feedings = new EntityStorage();
    private final EntityStorage visitors = new EntityStorage();
    private final FoodInventory inventory = new FoodInventory();

    public Zoo(String name) { this.name = Entity.text(name); }
    public String getName() { return name; }
    public EntityStorage getAnimals() { return animals; }
    public EntityStorage getEnclosures() { return enclosures; }
    public EntityStorage getKeepers() { return keepers; }
    public EntityStorage getPlans() { return plans; }
    public EntityStorage getVets() { return vets; }
    public EntityStorage getFeedings() { return feedings; }
    public EntityStorage getVisitors() { return visitors; }
    public FoodInventory getInventory() { return inventory; }
    @Override public String toString() { return name + ": животных " + animals.size(); }
}
