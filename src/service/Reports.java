package service;
import model.*;

public class Reports {
    private final ZooService service;
    public Reports(ZooService service) { this.service = service; }

    public void foodCosts() {
        Zoo zoo = service.getZoo();
        long total = 0;
        System.out.printf("%-20s %12s %15s%n", "Корм", "Съедено, кг", "Затраты, руб.");
        for (FoodType food : FoodType.values()) {
            double kilograms = 0;
            long cost = 0;
            for (int i = 0; i < zoo.getFeedings().size(); i++) {
                FeedingEvent event = (FeedingEvent) zoo.getFeedings().get(i);
                if (event.getFood() == food) {
                    kilograms += event.getKilograms();
                    cost = Math.addExact(cost, event.getCostKopecks());
                }
            }
            total = Math.addExact(total, cost);
            System.out.printf("%-20s %12.3f %15.2f%n", food, kilograms, cost / 100.0);
        }
        System.out.printf("Итого за всю историю: %.2f руб.%n", total / 100.0);
    }
    public void occupancy() {
        Zoo zoo = service.getZoo();
        System.out.printf("%-4s %-22s %8s %8s %-20s%n", "ID", "Вольер", "Занято", "Мест", "Смотритель");
        for (int i = 0; i < zoo.getEnclosures().size(); i++) {
            Enclosure e = (Enclosure) zoo.getEnclosures().get(i);
            String keeper = e.getKeeperId() == 0 ? "Не назначен" : service.keeper(e.getKeeperId()).getName();
            System.out.printf("%-4d %-22.22s %8d %8d %-20.20s%n",
                    e.getId(), e.getName(), service.occupancy(e.getId()), e.getCapacity(), keeper);
        }
        System.out.println("Группировка животных по рациону:");
        for (Diet diet : Diet.values()) {
            int count = 0;
            double ration = 0;
            for (Animal animal : service.animals()) if (animal.getDiet() == diet) {
                count++;
                ration += animal.dailyFoodKg();
            }
            System.out.printf("%-15s %4d животных, %10.3f кг/сутки%n", diet, count, ration);
        }
    }
    public void sickAnimals() {
        int count = 0;
        System.out.printf("%-4s %-18s %-12s %s%n", "ID", "Животное", "Осмотр", "Диагноз");
        for (Animal animal : service.animals()) {
            VetRecord latest = service.latestExam(animal.getId());
            if (latest != null && latest.isSick()) {
                System.out.printf("%-4d %-18.18s %-12s %s%n", animal.getId(),
                        animal.getName(), latest.getDate(), latest.getDiagnosis());
                count++;
            }
        }
        System.out.println("Больных: " + count + ". Животные без осмотра не считаются обследованными.");
    }
    public void tickets() {
        Zoo zoo = service.getZoo();
        long total = 0;
        System.out.printf("%-12s %10s %15s%n", "Билет", "Продано", "Выручка, руб.");
        for (TicketType type : TicketType.values()) {
            int count = 0;
            long revenue = 0;
            for (int i = 0; i < zoo.getVisitors().size(); i++) {
                Visitor visitor = (Visitor) zoo.getVisitors().get(i);
                if (visitor.getTicket() == type) {
                    count++;
                    revenue = Math.addExact(revenue, visitor.getPaidKopecks());
                }
            }
            total = Math.addExact(total, revenue);
            System.out.printf("%-12s %10d %15.2f%n", type, count, revenue / 100.0);
        }
        System.out.printf("Общая выручка: %.2f руб.%n", total / 100.0);
    }
}
