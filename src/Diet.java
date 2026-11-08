public enum Diet {
    HERBIVORE("травоядное"),
    CARNIVORE("хищник"),
    OMNIVORE("всеядное");

    private final String title;

    Diet(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
