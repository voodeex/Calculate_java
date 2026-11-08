import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public class Task5Students {
    public static void main(String[] args) {
        ArrayList<Student> students = new ArrayList<Student>();
        students.add(new Student("Анна", 19, 4.8));
        students.add(new Student("Иван", 17, 3.9));
        students.add(new Student("Мария", 20, 4.6));
        students.add(new Student("Олег", 18, 4.1));
        students.add(new Student("Елена", 16, 4.9));
        students.add(new Student("Дмитрий", 21, 3.7));
        students.add(new Student("София", 22, 5.0));
        students.add(new Student("Павел", 18, 4.5));

        System.out.println("Все студенты:");
        for (Student student : students) {
            System.out.println(student);
        }

        Iterator<Student> iterator = students.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getAge() < 18) {
                iterator.remove();
            }
        }

        if (students.isEmpty()) {
            System.out.println("После удаления несовершеннолетних список пуст.");
            return;
        }

        Student best = students.get(0);
        ArrayList<Student> excellentStudents = new ArrayList<Student>();
        for (Student student : students) {
            if (student.getAverageGrade() > best.getAverageGrade()) {
                best = student;
            }
            if (student.isExcellent()) {
                excellentStudents.add(student);
            }
        }
        System.out.println("Лучший студент: " + best);

        String[] excellentNames = new String[excellentStudents.size()];
        for (int i = 0; i < excellentStudents.size(); i++) {
            excellentNames[i] = excellentStudents.get(i).getName();
        }
        System.out.println("Имена отличников: " + Arrays.toString(excellentNames));

        students.sort(Comparator.comparingDouble(Student::getAverageGrade).reversed());
        System.out.println("По убыванию среднего балла:");
        for (Student student : students) {
            System.out.println(student);
        }
    }
}
