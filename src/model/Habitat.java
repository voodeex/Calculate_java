package model;

public enum Habitat {
    LAND("Наземный"), AVIARY("Птичник / лётный вольер"), WATER("Водный");

    private final String title;
    Habitat(String title) { this.title = title; }
    public boolean accepts(Animal animal) { return animal.getHabitat() == this; }
    @Override public String toString() { return title; }
}
