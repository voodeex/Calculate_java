package model;
import java.time.LocalDate;

public class Visitor extends Entity {
    private final String name;
    private final LocalDate date;
    private final TicketType ticket;
    private final long paidKopecks;

    public Visitor(int id, String name, LocalDate date, TicketType ticket, long paidKopecks) {
        super(id);
        if (date == null || date.isAfter(LocalDate.now()) || ticket == null
                || paidKopecks < 0 || paidKopecks > 100_000_000)
            throw new IllegalArgumentException("Некорректные данные билета");
        this.name = text(name);
        this.date = date;
        this.ticket = ticket;
        this.paidKopecks = paidKopecks;
    }
    public String getName() { return name; }
    public LocalDate getDate() { return date; }
    public TicketType getTicket() { return ticket; }
    public long getPaidKopecks() { return paidKopecks; }
    @Override public String toString() {
        return String.format("%4d %-20.20s %s %-10s %8.2f руб.",
                getId(), name, date, ticket, paidKopecks / 100.0);
    }
}
