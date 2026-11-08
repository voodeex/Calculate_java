package model;

public enum TicketType {
    ADULT("Взрослый", 90000), CHILD("Детский", 45000), BENEFIT("Льготный", 30000);

    private final String title;
    private final long priceKopecks;
    TicketType(String title, long priceKopecks) {
        this.title = title;
        this.priceKopecks = priceKopecks;
    }
    public long getPriceKopecks() { return priceKopecks; }
    @Override public String toString() { return title; }
}
