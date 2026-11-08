import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Practice3 {
    private static final Random RANDOM = new Random();
    private static Integer previousRandom;

    public static void main(String[] args) {
        System.out.println("Отрицательное ли -7: " + isNegative(-7));
        System.out.println("Длина строки: " + stringLength("Java"));
        System.out.println("Последний символ: " + lastCharacter("Java"));
        System.out.println("Чётное ли 24: " + isEven(24));
        System.out.println("Первые буквы совпадают: " + sameFirstLetters("кот", "кит"));
        System.out.println("Первая цифра -1234: " + firstDigit(-1234));
        System.out.println("Последняя цифра -1234: " + lastDigit(-1234));
        System.out.println("Сумма первой и последней цифр: " + firstAndLastDigitSum(1234));
        System.out.println("Количество цифр: " + digitCount(-1234));
        System.out.println("Первые цифры совпадают: " + sameFirstDigits(123, 198));
        printOneToHundred();
        System.out.println("Предпоследний символ: " + penultimateCharacter("Java"));
        System.out.println("Остаток от деления: " + remainder(17, 5));
        System.out.println("Пора года для месяца 1: " + season(1));
        System.out.println("Делятся на 5: " + Arrays.toString(divisibleByFive(
                new int[]{1, 5, 10, 13, 25})));
        System.out.println("Первые три символа: " + firstThree("abcdef"));
        System.out.println("Регистр 'a': " + characterCase('a'));
        System.out.println("Первый срез: " + Arrays.toString(firstThreeElements(
                new int[]{1, 2, 3, 4, 5, 6})));
        System.out.println("Позиция первого нуля: " + firstZeroIndex(
                new int[]{1, 2, 3, 0, 4, 5}));
        System.out.println("Замена дефисов: " + replaceHyphens("123-456-789"));
        System.out.println("Строки с http://: " + Arrays.toString(httpOnly(
                new String[]{"http://example.org", "https://example.org", "http://test.ru"})));
        System.out.println("Первый ноль в строке: " + firstZeroIndex("1203405"));
        System.out.println("Последние три символа: " + lastThree("abcdef"));
        System.out.println("Второй срез: " + Arrays.toString(middleSlice(
                new int[]{1, 2, 3, 4, 5, 6})));
        System.out.println("Массив слов: " + Arrays.toString(splitWords("abc def jhi")));
        System.out.println("Повторяющиеся цифры в 133: " + hasDuplicateDigits(133));
        printMatrix(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
        printPyramid(5);
        System.out.println("Строка состоит из цифр: " + isIntegerString("1203405"));
        System.out.println("Строка является дробью: " + isDecimalString("-12.50"));
        System.out.println("Второе по величине: " + secondLargest(new int[]{7, 3, 7, 5, 2}));
        System.out.println("Диапазон: " + Arrays.toString(range(8, 3)));
        char[] letters = new char[8];
        fillRandomLetters(letters);
        System.out.println("Случайные буквы: " + Arrays.toString(letters));
        System.out.println("Сумма первых 10 чисел Фибоначчи: " + fibonacciSum(10));
        System.out.println("Дата существует: " + isValidDate("2024-02-29"));
        System.out.println("Суток в 172800 секундах: " + secondsToDays(172800));
        System.out.println("Случайная строка: " + randomLatinString(12));
        System.out.println("Аббревиатура: " + initials("java virtual machine"));
        System.out.println("Массивы делителей: " + deepToString(divisorsForEach(
                new int[]{6, 10, 15})));
        System.out.println("Слова по алфавиту: " + sortWords("груша яблоко абрикос"));
        System.out.println("Общие элементы: " + Arrays.toString(intersection(
                new int[]{1, 2, 2, 3, 5}, new int[]{2, 3, 4, 5})));
        System.out.println("Два разных случайных числа подряд: "
                + nextNonRepeatingRandom(10) + ", " + nextNonRepeatingRandom(10));
        System.out.println("Без избыточных дублей: " + Arrays.toString(removeValuesRepeatedOverThree(
                new int[]{1, 1, 1, 1, 2, 2, 3, 3, 3})));
        System.out.println("Самое длинное слово: " + longestWord("Java делает код понятным"));
        System.out.println("Даты с четырьмя двойками: " + Arrays.toString(datesWithFourTwos(2001)));
        int year = Year.now().getValue();
        System.out.println("Пятниц 13-е в " + year + " году: " + fridayTheThirteenths(year));
        System.out.println("Строка-последовательность: " + buildSequenceString(1, 10));
        System.out.println("Можно собрать счастливый билет: " + canMakeLuckyTicket(
                new int[]{1, 2, 3, 3, 2, 1}));
        System.out.println("Случайное слово на 'к': " + randomWordStartingWith(
                new String[]{"кот", "дом", "кит", "мир"}, 'к'));
        System.out.println("Множественное число city: " + pluralizeEnglishNoun("city"));
        printAmicablePairs(1, 10_000);
        System.out.println("Разложение 360: " + primeFactorization(360));

        int[][] diagonal = {
                {11, 12, 13, 14, 15},
                {21, 22, 23, 24, 25},
                {31, 32, 33, 34, 35},
                {41, 42, 43, 44, 45},
                {51, 52, 53, 54, 55}
        };
        zeroMainDiagonal(diagonal);
        printMatrix(diagonal);
        System.out.println(nounAfterNumber(1223421, "яблоко", "яблока", "яблок"));

        if (args.length == 1 && "--input".equals(args[0])) {
            consoleSum();
        } else {
            System.out.println("Сумма двух чисел (демо): " + sum(12, 30));
        }
    }

    public static boolean isNegative(int number) {
        return number < 0;
    }

    public static int stringLength(String value) {
        return value == null ? 0 : value.length();
    }

    public static char lastCharacter(String value) {
        requireNonEmpty(value);
        return value.charAt(value.length() - 1);
    }

    public static boolean isEven(int number) {
        return number % 2 == 0;
    }

    public static boolean sameFirstLetters(String first, String second) {
        requireNonEmpty(first);
        requireNonEmpty(second);
        return Character.toLowerCase(first.charAt(0)) == Character.toLowerCase(second.charAt(0));
    }

    public static int firstDigit(int number) {
        long value = Math.abs((long) number);
        while (value >= 10) {
            value /= 10;
        }
        return (int) value;
    }

    public static int lastDigit(int number) {
        return (int) (Math.abs((long) number) % 10);
    }

    public static int firstAndLastDigitSum(int number) {
        return firstDigit(number) + lastDigit(number);
    }

    public static int digitCount(int number) {
        return String.valueOf(Math.abs((long) number)).length();
    }

    public static boolean sameFirstDigits(int first, int second) {
        return firstDigit(first) == firstDigit(second);
    }

    public static void printOneToHundred() {
        StringBuilder result = new StringBuilder("Числа от 1 до 100: ");
        for (int number = 1; number <= 100; number++) {
            if (number > 1) {
                result.append(' ');
            }
            result.append(number);
        }
        System.out.println(result);
    }

    public static char penultimateCharacter(String value) {
        if (value == null || value.length() < 2) {
            throw new IllegalArgumentException("Нужно не менее двух символов");
        }
        return value.charAt(value.length() - 2);
    }

    public static int remainder(int first, int second) {
        if (second == 0) {
            throw new IllegalArgumentException("Деление на ноль запрещено");
        }
        return first % second;
    }

    public static String season(int month) {
        switch (month) {
            case 12:
            case 1:
            case 2:
                return "зима";
            case 3:
            case 4:
            case 5:
                return "весна";
            case 6:
            case 7:
            case 8:
                return "лето";
            case 9:
            case 10:
            case 11:
                return "осень";
            default:
                throw new IllegalArgumentException("Месяц должен быть от 1 до 12");
        }
    }

    public static int[] divisibleByFive(int[] values) {
        int count = 0;
        for (int value : values) {
            if (value % 5 == 0) {
                count++;
            }
        }
        int[] result = new int[count];
        int index = 0;
        for (int value : values) {
            if (value % 5 == 0) {
                result[index++] = value;
            }
        }
        return result;
    }

    public static String firstThree(String value) {
        if (value == null || value.length() < 3) {
            throw new IllegalArgumentException("Нужно не менее трёх символов");
        }
        return value.substring(0, 3);
    }

    public static String characterCase(char character) {
        if (Character.isUpperCase(character)) {
            return "верхний";
        }
        if (Character.isLowerCase(character)) {
            return "нижний";
        }
        return "не буква";
    }

    public static int[] firstThreeElements(int[] values) {
        if (values.length < 3) {
            throw new IllegalArgumentException("Нужно не менее трёх элементов");
        }
        return Arrays.copyOfRange(values, 0, 3);
    }

    public static int firstZeroIndex(int[] values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] == 0) {
                return i;
            }
        }
        return -1;
    }

    public static String replaceHyphens(String value) {
        return value == null ? null : value.replace('-', ' ');
    }

    public static String[] httpOnly(String[] values) {
        int count = 0;
        for (String value : values) {
            if (value != null && value.startsWith("http://")) {
                count++;
            }
        }
        String[] result = new String[count];
        int index = 0;
        for (String value : values) {
            if (value != null && value.startsWith("http://")) {
                result[index++] = value;
            }
        }
        return result;
    }

    public static int firstZeroIndex(String value) {
        return value == null ? -1 : value.indexOf('0');
    }

    public static String lastThree(String value) {
        if (value == null || value.length() < 3) {
            throw new IllegalArgumentException("Нужно не менее трёх символов");
        }
        return value.substring(value.length() - 3);
    }

    public static int[] middleSlice(int[] values) {
        if (values.length < 5) {
            throw new IllegalArgumentException("Нужно не менее пяти элементов");
        }
        return Arrays.copyOfRange(values, 2, 5);
    }

    public static String[] splitWords(String value) {
        String normalized = value == null ? "" : value.trim();
        return normalized.isEmpty() ? new String[0] : normalized.split("\\s+");
    }

    public static void consoleSum() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите два целых числа: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Первое значение не является целым числом.");
            return;
        }
        int first = scanner.nextInt();
        if (!scanner.hasNextInt()) {
            System.out.println("Второе значение не является целым числом.");
            return;
        }
        int second = scanner.nextInt();
        System.out.println("Сумма: " + sum(first, second));
    }

    public static int sum(int first, int second) {
        return first + second;
    }

    public static boolean hasDuplicateDigits(int number) {
        boolean[] seen = new boolean[10];
        long value = Math.abs((long) number);
        do {
            int digit = (int) (value % 10);
            if (seen[digit]) {
                return true;
            }
            seen[digit] = true;
            value /= 10;
        } while (value > 0);
        return false;
    }

    public static void printMatrix(int[][] matrix) {
        System.out.println("Двумерный массив:");
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    public static void printPyramid(int height) {
        for (int row = 1; row <= height; row++) {
            char[] line = new char[row];
            Arrays.fill(line, 'x');
            System.out.println(new String(line));
        }
    }

    public static boolean isIntegerString(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isDecimalString(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        int separatorCount = 0;
        int digits = 0;
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (i == 0 && (character == '-' || character == '+')) {
                continue;
            }
            if (character == '.' || character == ',') {
                separatorCount++;
                if (separatorCount > 1 || digits == 0 || i == value.length() - 1) {
                    return false;
                }
            } else if (Character.isDigit(character)) {
                digits++;
            } else {
                return false;
            }
        }
        return separatorCount == 1;
    }

    public static int secondLargest(int[] values) {
        if (values == null || values.length < 2) {
            throw new IllegalArgumentException("Нужно не менее двух чисел");
        }
        Integer largest = null;
        Integer second = null;
        for (int value : values) {
            if (largest == null || value > largest) {
                if (largest == null || value != largest) {
                    second = largest;
                }
                largest = value;
            } else if (value != largest && (second == null || value > second)) {
                second = value;
            }
        }
        if (second == null) {
            throw new IllegalArgumentException("Нет второго различного по величине числа");
        }
        return second;
    }

    public static int[] range(int first, int second) {
        int min = Math.min(first, second);
        int max = Math.max(first, second);
        int[] result = new int[max - min + 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = min + i;
        }
        return result;
    }

    public static void fillRandomLetters(char[] values) {
        for (int i = 0; i < values.length; i++) {
            values[i] = (char) ('a' + RANDOM.nextInt(26));
        }
    }

    public static long fibonacciSum(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Количество не может быть отрицательным");
        }
        long previous = 0;
        long current = 1;
        long sum = 0;
        for (int i = 0; i < count; i++) {
            sum = Math.addExact(sum, previous);
            long next = Math.addExact(previous, current);
            previous = current;
            current = next;
        }
        return sum;
    }

    public static boolean isValidDate(String value) {
        try {
            LocalDate.parse(value);
            return true;
        } catch (DateTimeException exception) {
            return false;
        }
    }

    public static long secondsToDays(long seconds) {
        return seconds / 86_400;
    }

    public static String randomLatinString(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Длина не может быть отрицательной");
        }
        char[] result = new char[length];
        fillRandomLetters(result);
        return new String(result);
    }

    public static String initials(String words) {
        String[] parts = splitWords(words);
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            result.append(Character.toUpperCase(part.charAt(0)));
        }
        return result.toString();
    }

    public static int[][] divisorsForEach(int[] values) {
        int[][] result = new int[values.length][];
        for (int i = 0; i < values.length; i++) {
            result[i] = divisors(values[i]);
        }
        return result;
    }

    public static int[] divisors(int number) {
        int absolute = Math.abs(number);
        if (absolute == 0) {
            return new int[0];
        }
        int count = 0;
        for (int candidate = 1; candidate <= absolute; candidate++) {
            if (absolute % candidate == 0) {
                count++;
            }
        }
        int[] result = new int[count];
        int index = 0;
        for (int candidate = 1; candidate <= absolute; candidate++) {
            if (absolute % candidate == 0) {
                result[index++] = candidate;
            }
        }
        return result;
    }

    public static String sortWords(String text) {
        String[] words = splitWords(text);
        Arrays.sort(words, String.CASE_INSENSITIVE_ORDER);
        return String.join(" ", words);
    }

    public static int[] intersection(int[] first, int[] second) {
        int[] temporary = new int[Math.min(first.length, second.length)];
        int count = 0;
        for (int value : first) {
            if (contains(second, value) && !contains(Arrays.copyOf(temporary, count), value)) {
                temporary[count++] = value;
            }
        }
        return Arrays.copyOf(temporary, count);
    }

    public static int nextNonRepeatingRandom(int bound) {
        if (bound < 2) {
            throw new IllegalArgumentException("Граница должна быть не меньше 2");
        }
        int value;
        do {
            value = RANDOM.nextInt(bound);
        } while (previousRandom != null && value == previousRandom);
        previousRandom = value;
        return value;
    }

    public static int[] removeValuesRepeatedOverThree(int[] values) {
        int[] result = new int[values.length];
        int size = 0;
        for (int value : values) {
            if (countOccurrences(values, value) <= 3) {
                result[size++] = value;
            }
        }
        return Arrays.copyOf(result, size);
    }

    public static String longestWord(String text) {
        String[] words = splitWords(text.replaceAll("[^\\p{L}\\p{Nd}]+", " "));
        String longest = "";
        for (String word : words) {
            if (word.length() > longest.length()) {
                longest = word;
            }
        }
        return longest;
    }

    public static String[] datesWithFourTwos(int centuryStartYear) {
        String[] temporary = new String[366 * 100];
        int count = 0;
        LocalDate date = LocalDate.of(centuryStartYear, 1, 1);
        LocalDate end = date.plusYears(100);
        while (date.isBefore(end)) {
            String formatted = String.format("%02d.%02d.%04d",
                    date.getDayOfMonth(), date.getMonthValue(), date.getYear());
            if (countCharacter(formatted, '2') == 4) {
                temporary[count++] = formatted;
            }
            date = date.plusDays(1);
        }
        return Arrays.copyOf(temporary, count);
    }

    public static int fridayTheThirteenths(int year) {
        int count = 0;
        for (int month = 1; month <= 12; month++) {
            if (LocalDate.of(year, month, 13).getDayOfWeek() == DayOfWeek.FRIDAY) {
                count++;
            }
        }
        return count;
    }

    public static String buildSequenceString(int start, int end) {
        StringBuilder result = new StringBuilder();
        int step = start <= end ? 1 : -1;
        for (int value = start; ; value += step) {
            if (result.length() > 0) {
                result.append('-');
            }
            result.append(value);
            if (value == end) {
                break;
            }
        }
        return result.toString();
    }

    public static boolean canMakeLuckyTicket(int[] digits) {
        if (digits == null || digits.length != 6) {
            return false;
        }
        for (int digit : digits) {
            if (digit < 0 || digit > 9) {
                return false;
            }
        }
        for (int first = 0; first < 6; first++) {
            for (int second = first + 1; second < 6; second++) {
                for (int third = second + 1; third < 6; third++) {
                    int left = digits[first] + digits[second] + digits[third];
                    int right = 0;
                    for (int i = 0; i < 6; i++) {
                        if (i != first && i != second && i != third) {
                            right += digits[i];
                        }
                    }
                    if (left == right) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String randomWordStartingWith(String[] words, char letter) {
        int count = 0;
        for (String word : words) {
            if (word != null && !word.isEmpty()
                    && Character.toLowerCase(word.charAt(0)) == Character.toLowerCase(letter)) {
                count++;
            }
        }
        if (count == 0) {
            return null;
        }
        int selected = RANDOM.nextInt(count);
        for (String word : words) {
            if (word != null && !word.isEmpty()
                    && Character.toLowerCase(word.charAt(0)) == Character.toLowerCase(letter)) {
                if (selected-- == 0) {
                    return word;
                }
            }
        }
        return null;
    }

    public static String pluralizeEnglishNoun(String noun) {
        if (noun == null || noun.isEmpty()) {
            throw new IllegalArgumentException("Существительное не задано");
        }
        String lower = noun.toLowerCase();
        if (lower.endsWith("y") && noun.length() > 1
                && !isEnglishVowel(lower.charAt(lower.length() - 2))) {
            return noun.substring(0, noun.length() - 1) + "ies";
        }
        if (lower.endsWith("s") || lower.endsWith("x") || lower.endsWith("z")
                || lower.endsWith("ch") || lower.endsWith("sh")) {
            return noun + "es";
        }
        if (lower.endsWith("f")) {
            return noun.substring(0, noun.length() - 1) + "ves";
        }
        if (lower.endsWith("fe")) {
            return noun.substring(0, noun.length() - 2) + "ves";
        }
        return noun + "s";
    }

    public static void printAmicablePairs(int start, int end) {
        System.out.println("Дружественные числа:");
        for (int number = Math.max(2, start); number <= end; number++) {
            int pair = properDivisorSum(number);
            if (pair > number && pair <= end && properDivisorSum(pair) == number) {
                System.out.println(number + " и " + pair);
            }
        }
    }

    public static String primeFactorization(int number) {
        if (number == 0 || number == 1 || number == -1) {
            return number + " не раскладывается на простые множители";
        }
        StringBuilder result = new StringBuilder(number + " = ");
        long value = number;
        if (value < 0) {
            result.append("-1 * ");
            value = -value;
        }
        boolean first = true;
        for (long factor = 2; factor * factor <= value; factor++) {
            while (value % factor == 0) {
                if (!first) {
                    result.append(" * ");
                }
                result.append(factor);
                value /= factor;
                first = false;
            }
        }
        if (value > 1) {
            if (!first) {
                result.append(" * ");
            }
            result.append(value);
        }
        return result.toString();
    }

    public static void zeroMainDiagonal(int[][] matrix) {
        for (int row = 0; row < matrix.length; row++) {
            if (matrix[row] != null && row < matrix[row].length) {
                matrix[row][row] = 0;
            }
        }
    }

    public static String nounAfterNumber(long number, String one, String twoToFour, String many) {
        long absolute = Math.abs(number);
        long lastTwo = absolute % 100;
        String form;
        if (lastTwo >= 11 && lastTwo <= 14) {
            form = many;
        } else {
            switch ((int) (absolute % 10)) {
                case 1:
                    form = one;
                    break;
                case 2:
                case 3:
                case 4:
                    form = twoToFour;
                    break;
                default:
                    form = many;
            }
        }
        return number + " " + form;
    }

    private static void requireNonEmpty(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Строка пуста");
        }
    }

    private static boolean contains(int[] values, int target) {
        for (int value : values) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }

    private static int countOccurrences(int[] values, int target) {
        int count = 0;
        for (int value : values) {
            if (value == target) {
                count++;
            }
        }
        return count;
    }

    private static int countCharacter(String value, char target) {
        int count = 0;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == target) {
                count++;
            }
        }
        return count;
    }

    private static boolean isEnglishVowel(char character) {
        return "aeiou".indexOf(Character.toLowerCase(character)) >= 0;
    }

    private static int properDivisorSum(int number) {
        if (number <= 1) {
            return 0;
        }
        int sum = 1;
        for (int divisor = 2; divisor * divisor <= number; divisor++) {
            if (number % divisor == 0) {
                sum += divisor;
                int pair = number / divisor;
                if (pair != divisor) {
                    sum += pair;
                }
            }
        }
        return sum;
    }

    private static String deepToString(int[][] values) {
        return Arrays.deepToString(values);
    }
}
