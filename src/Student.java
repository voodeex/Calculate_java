public class Student {
    private final String name;
    private final int age;
    private final double averageGrade;

    public Student(String name, int age, double averageGrade) {
        this.name = name;
        this.age = age;
        this.averageGrade = averageGrade;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public double getAverageGrade() {
        return averageGrade;
    }

    public boolean isExcellent() {
        return averageGrade >= 4.5;
    }

    @Override
    public String toString() {
        return String.format("Student{name='%s', age=%d, averageGrade=%.2f}",
                name, age, averageGrade);
    }
}
