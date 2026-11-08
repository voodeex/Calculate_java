package model;

public enum Climate {
    TEMPERATE("Умеренный"), TROPICAL("Тропический"),
    ARID("Засушливый"), COLD("Холодный");

    private final String title;
    Climate(String title) { this.title = title; }
    public boolean suits(Climate required) { return this == required; }
    @Override public String toString() { return title; }
}
