package service;

import exception.IncompatibleAnimalsException;
import exception.ZooOperationException;
import model.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ZooService {
    private final Zoo zoo;
    private final Logger logger;

    public ZooService(Zoo zoo, Logger logger) {
        if (zoo == null || logger == null) throw new IllegalArgumentException("Нет данных");
        this.zoo = zoo;
        this.logger = logger;
    }
    public Zoo getZoo() { return zoo; }
    public Animal animal(int id) {
        Entity entity = zoo.getAnimals().find(id);
        if (entity == null) throw new ZooOperationException("Животное не найдено");
        return (Animal) entity;
    }
    public Enclosure enclosure(int id) {
        Entity entity = zoo.getEnclosures().find(id);
        if (entity == null) throw new ZooOperationException("Вольер не найден");
        return (Enclosure) entity;
    }
    public Keeper keeper(int id) {
        Entity entity = zoo.getKeepers().find(id);
        if (entity == null) throw new ZooOperationException("Смотритель не найден");
        return (Keeper) entity;
    }
    public Animal[] animals() {
        Animal[] result = new Animal[zoo.getAnimals().size()];
        for (int i = 0; i < result.length; i++) result[i] = (Animal) zoo.getAnimals().get(i);
        return result;
    }
    public void addKeeper(Keeper keeper) {
        zoo.getKeepers().add(keeper);
        logger.log("Добавлен смотритель " + keeper.getId());
    }
    public void addEnclosure(Enclosure enclosure) {
        if (enclosure.getKeeperId() != 0) keeper(enclosure.getKeeperId());
        zoo.getEnclosures().add(enclosure);
        logger.log("Добавлен вольер " + enclosure.getId());
    }
    public int occupancy(int enclosureId) {
        int count = 0;
        for (Animal a : animals()) if (a.getEnclosureId() == enclosureId) count++;
        return count;
    }
    private void checkPlacement(Animal animal, int enclosureId)
            throws IncompatibleAnimalsException {
        Enclosure target = enclosure(enclosureId);
        if (!target.getClimate().suits(animal.getClimate()))
            throw new IncompatibleAnimalsException("Климат вольера не подходит животному");
        if (!target.getHabitat().accepts(animal))
            throw new IncompatibleAnimalsException("Тип вольера не подходит животному");
        int count = 0;
        for (Animal other : animals()) {
            if (other.getId() == animal.getId() || other.getEnclosureId() != enclosureId) continue;
            count++;
            if (!animal.getDiet().compatible(other.getDiet()))
                throw new IncompatibleAnimalsException("Хищник и травоядное не могут жить вместе");
        }
        if (count >= target.getCapacity())
            throw new IncompatibleAnimalsException("Вольер заполнен");
    }
    public void addAnimal(Animal animal, int enclosureId) throws IncompatibleAnimalsException {
        if (animal == null) throw new IllegalArgumentException("Животное не задано");
        checkPlacement(animal, enclosureId);
        zoo.getAnimals().add(animal);
        animal.setEnclosureId(enclosureId);
        logger.log("Добавлено животное " + animal.getId() + ", вольер " + enclosureId);
    }
    public void move(int animalId, int enclosureId) throws IncompatibleAnimalsException {
        Animal animal = animal(animalId);
        checkPlacement(animal, enclosureId);
        int old = animal.getEnclosureId();
        animal.setEnclosureId(enclosureId);
        logger.log("Перемещение " + animalId + ": " + old + " -> " + enclosureId);
    }
    public void assignKeeper(int enclosureId, int keeperId) {
        keeper(keeperId);
        enclosure(enclosureId).setKeeperId(keeperId);
        logger.log("Смотритель " + keeperId + " закреплён за вольером " + enclosureId);
    }
    public void addPlan(FeedingPlan plan) {
        animal(plan.getAnimalId());
        for (int i = 0; i < zoo.getPlans().size(); i++)
            if (((FeedingPlan) zoo.getPlans().get(i)).getAnimalId() == plan.getAnimalId())
                throw new ZooOperationException("У животного уже есть план кормления");
        zoo.getPlans().add(plan);
        logger.log("Добавлен план кормления " + plan.getId());
    }
    public void schedule(int animalId, int hour, int minute) {
        animal(animalId);
        for (int i = 0; i < zoo.getPlans().size(); i++) {
            FeedingPlan plan = (FeedingPlan) zoo.getPlans().get(i);
            if (plan.getAnimalId() == animalId) {
                plan.setTime(hour, minute);
                logger.log("Изменено время кормления животного " + animalId);
                return;
            }
        }
        addPlan(new FeedingPlan(zoo.getPlans().nextId(), animalId, hour, minute, null));
    }
    public void restock(FoodType food, double kilograms) {
        zoo.getInventory().add(food, kilograms);
        logger.log("Пополнение: " + food + ", " + kilograms + " кг");
    }
    public int feedDue(LocalDateTime now) {
        if (now == null || now.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("Некорректное время кормления");
        double[] needed = new double[FoodType.values().length];
        for (int i = 0; i < zoo.getPlans().size(); i++) {
            FeedingPlan plan = (FeedingPlan) zoo.getPlans().get(i);
            if (!plan.isDue(now)) continue;
            Animal animal = animal(plan.getAnimalId());
            Enclosure enclosure = enclosure(animal.getEnclosureId());
            keeper(enclosure.getKeeperId());
            needed[animal.getFood().ordinal()] += animal.dailyFoodKg();
        }
        for (FoodType food : FoodType.values())
            zoo.getInventory().require(food, needed[food.ordinal()]);
        int count = 0;
        for (int i = 0; i < zoo.getPlans().size(); i++) {
            FeedingPlan plan = (FeedingPlan) zoo.getPlans().get(i);
            if (!plan.isDue(now)) continue;
            Animal animal = animal(plan.getAnimalId());
            double amount = animal.dailyFoodKg();
            FeedingEvent event = new FeedingEvent(zoo.getFeedings().nextId(), plan.getId(),
                    animal.getId(), now, animal.getFood(), amount, animal.getFood().cost(amount));
            zoo.getFeedings().add(event);
            zoo.getInventory().consume(animal.getFood(), amount);
            plan.markFed(now.toLocalDate());
            logger.log("Кормление: " + event);
            count++;
        }
        return count;
    }
    public void addExam(VetRecord record) {
        animal(record.getAnimalId());
        zoo.getVets().add(record);
        logger.log("Ветеринарный осмотр: " + record);
    }
    public VetRecord latestExam(int animalId) {
        VetRecord latest = null;
        for (int i = 0; i < zoo.getVets().size(); i++) {
            VetRecord record = (VetRecord) zoo.getVets().get(i);
            if (record.getAnimalId() == animalId && (latest == null
                    || !record.getDate().isBefore(latest.getDate()))) latest = record;
        }
        return latest;
    }
    public Visitor sellTicket(String name, TicketType ticket) {
        if (ticket == null) throw new IllegalArgumentException("Билет не выбран");
        Visitor visitor = new Visitor(zoo.getVisitors().nextId(), name, LocalDate.now(),
                ticket, ticket.getPriceKopecks());
        zoo.getVisitors().add(visitor);
        logger.log("Продан билет: " + visitor);
        return visitor;
    }
    public void restoreFeeding(FeedingEvent event) {
        Animal animal = animal(event.getAnimalId());
        Entity entity = zoo.getPlans().find(event.getPlanId());
        if (!(entity instanceof FeedingPlan)
                || ((FeedingPlan) entity).getAnimalId() != animal.getId()
                || event.getFood() != animal.getFood())
            throw new ZooOperationException("Нарушена связь записи кормления");
        zoo.getFeedings().add(event);
        FeedingPlan plan = (FeedingPlan) entity;
        LocalDate date = event.getTime().toLocalDate();
        if (plan.getLastFedDate() == null || plan.getLastFedDate().isBefore(date)) plan.markFed(date);
    }
}
