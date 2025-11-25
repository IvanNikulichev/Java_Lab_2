package slidingk;

import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;

public class Main {

    // Быстрый сканер, читает данные из потока без использования класса Scanner
    private static class FastScanner {
        private final InputStream in;

        // Буфер байтов, в который сразу читается большой кусок данных
        private final byte[] buffer = new byte[1 << 16];

        // Текущая позиция в буфере
        private int ptr = 0;

        // Сколько байтов сейчас лежит в буфере (фактически прочитано из потока)
        private int len = 0;

        FastScanner(InputStream in) {
            this.in = in;
        }

        // Читает следующий байт из буфера, при необходимости заново заполняет буфер
        // Возвращает -1 при достижении конца потока
        private int read() throws IOException {
            if (ptr >= len) {
                len = in.read(buffer);
                ptr = 0;
                if (len <= 0) {
                    return -1;
                }
            }
            return buffer[ptr++];
        }

        // Чтение следующего целого числа (int), пропуская пробелы и переводы строки
        public int nextInt() throws IOException {
            int c = read();
            // Пропускаем все пробельные символы, пока не встретим цифру или '-'
            while (c <= ' ' && c != -1) {
                c = read();
            }
            int sign = 1;
            if (c == '-') {
                sign = -1;
                c = read();
            }
            int result = 0;
            // Накопление цифр в переменную result
            while (c > ' ') {
                result = result * 10 + (c - '0');
                c = read();
            }
            return result * sign;
        }

        // Чтение следующего long по тому же принципу, что и nextInt
        public long nextLong() throws IOException {
            int c = read();
            while (c <= ' ' && c != -1) {
                c = read();
            }
            int sign = 1;
            if (c == '-') {
                sign = -1;
                c = read();
            }
            long result = 0;
            while (c > ' ') {
                result = result * 10L + (c - '0');
                c = read();
            }
            return result * sign;
        }

        // Чтение следующего "слова" (последовательности непробельных символов)
        public String next() throws IOException {
            int c = read();
            // Пропускаем пробелы и переводы строк
            while (c <= ' ' && c != -1) {
                c = read();
            }
            StringBuilder sb = new StringBuilder();
            // Добавляем символы до первого пробельного
            while (c > ' ') {
                sb.append((char) c);
                c = read();
            }
            return sb.toString();
        }
    }

    public static void main(String[] args) throws Exception {
        // Создаём быстрый ввод на основе стандартного потока System.in
        FastScanner scanner = new FastScanner(System.in);

        // StringBuilder для накопления строк вывода (чтобы реже дергать System.out)
        StringBuilder output = new StringBuilder();

        // Считываем размер массива, количество операций и значение k (порядок статистики)
        int array_size = scanner.nextInt();
        int operations_count = scanner.nextInt();
        int order_index = scanner.nextInt();

        // Считываем исходный массив чисел
        long[] values = new long[array_size];
        for (int value_index = 0; value_index < array_size; ++value_index) {
            values[value_index] = scanner.nextLong();
        }

        // Строка с операциями 'L' и 'R', которые сдвигают границы окна
        String operations_string = scanner.next();

        // Копируем массив значений и сортируем его — подготовка к сжатию координат
        long[] sorted_values = values.clone();
        Arrays.sort(sorted_values);

        // Убираем дубликаты из отсортированного массива (получаем массив уникальных значений)
        int unique_count = 0;
        if (sorted_values.length > 0) {
            unique_count = 1;
            for (int i = 1; i < sorted_values.length; ++i) {
                // Каждый раз, когда встречаем новое значение — сдвигаем "хвост" массива
                if (sorted_values[i] != sorted_values[i - 1]) {
                    sorted_values[unique_count++] = sorted_values[i];
                }
            }
        }
        // Обрезаем массив до количества уникальных элементов
        sorted_values = Arrays.copyOf(sorted_values, unique_count);

        // compressed_values[i] — это индекс значения values[i] в массиве sorted_values
        // То есть мы заменяем реальные числа на их позиции в отсортированном массиве (сжатие координат)
        int[] compressed_values = new int[array_size];
        for (int value_index = 0; value_index < array_size; ++value_index) {
            long current_value = values[value_index];
            // Бинарный поиск позиции current_value в массиве sorted_values
            int left = 0;
            int right = sorted_values.length - 1;
            while (left < right) {
                int mid = (left + right) >>> 1;
                if (sorted_values[mid] < current_value) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }
            // Позиция текущего значения в массиве уникальных отсортированных значений
            compressed_values[value_index] = left;
        }

        // left_index и right_index задают текущие границы окна [left_index, right_index]
        int left_index = 0;
        int right_index = 0;

        // Создаём структуру для поддержки k-й порядковой статистики
        // в скользящем окне по массиву сжатых значений
        SlidingKStatistic window =
            new SlidingKStatistic(order_index, compressed_values, sorted_values);

        // Если массив не пустой — начинаем окно с одного элемента (индекс 0)
        if (array_size > 0) {
            window.Right(0);
        }

        // Обрабатываем все операции: 'L' — сдвиг левой границы, 'R' — сдвиг правой
        for (int operation_index = 0; operation_index < operations_count; ++operation_index) {
            char operation_type = operations_string.charAt(operation_index);
            if (operation_type == 'L') {
                // Сдвигаем левую границу окна вправо: удаляем элемент с индексом left_index
                window.Left(left_index);
                ++left_index;
            } else if (operation_type == 'R') {
                // Сдвигаем правую границу окна вправо: добавляем элемент с индексом right_index + 1
                ++right_index;
                window.Right(right_index);
            }
            // После каждой операции записываем в вывод текущее значение k-й статистики (или -1)
            output.append(window.GetKth()).append('\n');
        }

        // Печатаем накопленный результат одной операцией
        System.out.print(output.toString());
    }
}
