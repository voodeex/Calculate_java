import java.util.Arrays;

public class Zoo {
    private Animal[] animals = new Animal[4];
    private int animalCount;
    private Enclosure[] enclosures = new Enclosure[2];
    private int enclosureCount;

    public void addAnimal(Animal animal) throws ZooException {
        if (animal == null) {
            throw new ZooException("Животное не задано");
        }
        if (findAnimal(animal.getId()) != null) {
            throw new ZooException("Животное с id " + animal.getId() + " уже существует");
        }
        ensureAnimalCapacity();
        animals[animalCount++] = animal;
    }

    public void addEnclosure(Enclosure enclosure) throws ZooException {
        if (findEnclosure(enclosure.getId()) != null) {
            throw new ZooException("Вольер с таким id уже существует");
        }
        if (enclosureCount == enclosures.length) {
            enclosures = Arrays.copyOf(enclosures, enclosures.length * 2);
        }
        enclosures[enclosureCount++] = enclosure;
    }

    public Animal findAnimal(int id) {
        for (int i = 0; i < animalCount; i++) {
            if (animals[i].getId() == id) {
                return animals[i];
            }
        }
        return null;
    }

    public Enclosure findEnclosure(int id) {
        for (int i = 0; i < enclosureCount; i++) {
            if (enclosures[i].getId() == id) {
                return enclosures[i];
            }
        }
        return null;
    }

    public void renameAnimal(int id, String newName) throws ZooException {
        Animal animal = requireAnimal(id);
        animal.setName(newName);
    }

    public void deleteAnimal(int id) throws ZooException {
        int index = indexOfAnimal(id);
        if (index < 0) {
            throw new ZooException("Животное не найдено");
        }
        Animal animal = animals[index];
        if (animal.getEnclosureId() >= 0) {
            Enclosure enclosure = findEnclosure(animal.getEnclosureId());
            if (enclosure != null) {
                enclosure.remove(id);
            }
        }
        for (int i = index; i < animalCount - 1; i++) {
            animals[i] = animals[i + 1];
        }
        animals[--animalCount] = null;
    }

    public void moveAnimal(int animalId, int enclosureId) throws ZooException {
        Animal animal = requireAnimal(animalId);
        Enclosure target = findEnclosure(enclosureId);
        if (target == null) {
            throw new ZooException("Вольер не найден");
        }
        checkDietCompatibility(animal, target);
        target.add(animalId);
        if (animal.getEnclosureId() >= 0 && animal.getEnclosureId() != enclosureId) {
            Enclosure previous = findEnclosure(animal.getEnclosureId());
            if (previous != null) {
                previous.remove(animalId);
            }
        }
        animal.setEnclosureId(enclosureId);
    }

    public void showAnimals() {
        if (animalCount == 0) {
            System.out.println("Животных нет.");
            return;
        }
        for (int i = 0; i < animalCount; i++) {
            System.out.println(animals[i]);
        }
    }

    public void showEnclosures() {
        for (int i = 0; i < enclosureCount; i++) {
            System.out.println(enclosures[i]);
        }
    }

    public void feedAll() {
        double total = 0;
        for (int i = 0; i < animalCount; i++) {
            Animal animal = animals[i];
            double ration = animal.dailyFoodKg();
            total += ration;
            System.out.printf("%s получает %.2f кг корма. %s%n",
                    animal.getName(), ration, animal.makeSound());
        }
        System.out.printf("Суточный рацион всего зоопарка: %.2f кг%n", total);
    }

    private Animal requireAnimal(int id) throws ZooException {
        Animal animal = findAnimal(id);
        if (animal == null) {
            throw new ZooException("Животное не найдено");
        }
        return animal;
    }

    private int indexOfAnimal(int id) {
        for (int i = 0; i < animalCount; i++) {
            if (animals[i].getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private void checkDietCompatibility(Animal candidate, Enclosure enclosure) throws ZooException {
        for (int i = 0; i < animalCount; i++) {
            Animal existing = animals[i];
            if (enclosure.contains(existing.getId())) {
                boolean predatorWithHerbivore =
                        candidate.getDiet() == Diet.CARNIVORE && existing.getDiet() == Diet.HERBIVORE
                                || candidate.getDiet() == Diet.HERBIVORE
                                && existing.getDiet() == Diet.CARNIVORE;
                if (predatorWithHerbivore) {
                    throw new ZooException("Нельзя помещать хищника и травоядное в один вольер");
                }
            }
        }
    }

    private void ensureAnimalCapacity() {
        if (animalCount == animals.length) {
            animals = Arrays.copyOf(animals, animals.length * 2);
        }
    }
}
