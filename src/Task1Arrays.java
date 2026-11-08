import java.util.Arrays;
import java.util.Random;

public class Task1Arrays {
    public static void main(String[] args) {
        Random random = new Random();
        int[] numbers = new int[15];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = random.nextInt(101) - 50;
        }

        System.out.println("Исходный массив: " + Arrays.toString(numbers));
        if (numbers.length == 0) {
            System.out.println("Массив пуст.");
            return;
        }

        int min = numbers[0];
        int max = numbers[0];
        int sum = 0;
        int positiveCount = 0;
        int negativeCount = 0;
        int zeroCount = 0;
        for (int number : numbers) {
            min = Math.min(min, number);
            max = Math.max(max, number);
            sum += number;
            if (number > 0) {
                positiveCount++;
            } else if (number < 0) {
                negativeCount++;
            } else {
                zeroCount++;
            }
        }

        int[] positiveNumbers = new int[positiveCount];
        int positiveIndex = 0;
        for (int number : numbers) {
            if (number > 0) {
                positiveNumbers[positiveIndex++] = number;
            }
        }

        System.out.println("Минимум: " + min);
        System.out.println("Максимум: " + max);
        System.out.println("Сумма: " + sum);
        System.out.printf("Среднее: %.2f%n", (double) sum / numbers.length);
        System.out.printf("Положительных: %d, отрицательных: %d, нулей: %d%n",
                positiveCount, negativeCount, zeroCount);
        System.out.println("Только положительные: " + Arrays.toString(positiveNumbers));

        Arrays.sort(numbers);
        System.out.println("После сортировки: " + Arrays.toString(numbers));

        int[][] matrix = new int[4][5];
        System.out.println("Матрица 4x5:");
        for (int row = 0; row < matrix.length; row++) {
            int rowSum = 0;
            for (int column = 0; column < matrix[row].length; column++) {
                matrix[row][column] = random.nextInt(10);
                rowSum += matrix[row][column];
                System.out.print(matrix[row][column] + " ");
            }
            System.out.println("| сумма строки = " + rowSum);
        }
    }
}
