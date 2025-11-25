package slidingk;

import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;

public class Main {

    // Быстрый ввод
    private static class FastScanner {
        private final InputStream in;
        private final byte[] buffer = new byte[1 << 16];
        private int ptr = 0;
        private int len = 0;

        FastScanner(InputStream in) {
            this.in = in;
        }

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

        public int nextInt() throws IOException {
            int c = read();
            while (c <= ' ' && c != -1) {
                c = read();
            }
            int sign = 1;
            if (c == '-') {
                sign = -1;
                c = read();
            }
            int result = 0;
            while (c > ' ') {
                result = result * 10 + (c - '0');
                c = read();
            }
            return result * sign;
        }

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

        public String next() throws IOException {
            int c = read();
            while (c <= ' ' && c != -1) {
                c = read();
            }
            StringBuilder sb = new StringBuilder();
            while (c > ' ') {
                sb.append((char) c);
                c = read();
            }
            return sb.toString();
        }
    }

    // Дерево Фенвика: по заданию + поиск по порядку
    static class FenwickTree {
        private int size_;
        private int[] tree_;

        public FenwickTree() {
            this(0);
        }

        public FenwickTree(int size) {
            size_ = size;
            tree_ = new int[size_ + 1];
        }

        // Построение по массиву
        public void build(int[] arr) {
            size_ = arr.length;
            tree_ = new int[size_ + 1];
            for (int i = 0; i < size_; ++i) {
                internalAdd(i, arr[i]);
            }
        }

        // Обновление: arr[index] += delta
        public void update(int index, int delta) {
            internalAdd(index, delta);
        }

        // Сумма на префиксе [0..index]
        public int prefixSum(int index) {
            if (index < 0) {
                return 0;
            }
            if (size_ == 0) {
                return 0;
            }
            if (index >= size_) {
                index = size_ - 1;
            }
            int result = 0;
            int position = index + 1;
            while (position > 0) {
                result += tree_[position];
                position -= position & -position;
            }
            return result;
        }

        // Сумма на отрезке [left..right]
        public int rangeSum(int left, int right) {
            if (left > right) {
                return 0;
            }
            if (size_ == 0) {
                return 0;
            }
            if (right < 0) {
                return 0;
            }
            if (left < 0) {
                left = 0;
            }
            if (right >= size_) {
                right = size_ - 1;
            }
            int sumRight = prefixSum(right);
            int sumLeft = (left > 0) ? prefixSum(left - 1) : 0;
            return sumRight - sumLeft;
        }

        // Поиск индекса по порядку (для k-й статистики)
        public int findByOrder(int requiredOrder) {
            int currentIndex = 0;
            int bitMask = 1;
            while ((bitMask << 1) <= size_) {
                bitMask <<= 1;
            }
            int remainingOrder = requiredOrder;
            for (int step = bitMask; step > 0; step >>= 1) {
                int nextIndex = currentIndex + step;
                if (nextIndex <= size_ && tree_[nextIndex] < remainingOrder) {
                    remainingOrder -= tree_[nextIndex];
                    currentIndex = nextIndex;
                }
            }
            return currentIndex; // 0-based индекс сжатого значения
        }

        // Внутреннее добавление (используется build/update)
        private void internalAdd(int positionIndex, int delta) {
            int position = positionIndex + 1;
            while (position <= size_) {
                tree_[position] += delta;
                position += position & -position;
            }
        }
    }

    // Скользящая k-статистика
    static class SlidingKStatistic {
        private final int order_index_;
        private int window_size_;
        private final int[] compressed_values_;
        private final long[] sorted_values_;
        private final FenwickTree fenwick_;

        public SlidingKStatistic(int orderIndex,
                                 int[] compressedValues,
                                 long[] sortedValues) {
            order_index_ = orderIndex;
            window_size_ = 0;
            compressed_values_ = compressedValues;
            sorted_values_ = sortedValues;
            fenwick_ = new FenwickTree(sortedValues.length);
        }

        public void Right(int array_position) {
            int value_index = compressed_values_[array_position];
            fenwick_.update(value_index, 1);
            ++window_size_;
        }

        public void Left(int array_position) {
            int value_index = compressed_values_[array_position];
            fenwick_.update(value_index, -1);
            --window_size_;
        }

        public long GetKth() {
            if (window_size_ < order_index_) {
                return -1L;
            }
            int value_index = fenwick_.findByOrder(order_index_);
            return sorted_values_[value_index];
        }
    }

    public static void main(String[] args) throws Exception {
        FastScanner scanner = new FastScanner(System.in);
        StringBuilder output = new StringBuilder();

        int array_size = scanner.nextInt();
        int operations_count = scanner.nextInt();
        int order_index = scanner.nextInt();

        long[] values = new long[array_size];
        for (int value_index = 0; value_index < array_size; ++value_index) {
            values[value_index] = scanner.nextLong();
        }

        String operations_string = scanner.next();

        long[] sorted_values = values.clone();
        Arrays.sort(sorted_values);

        int unique_count = 0;
        if (sorted_values.length > 0) {
            unique_count = 1;
            for (int i = 1; i < sorted_values.length; ++i) {
                if (sorted_values[i] != sorted_values[i - 1]) {
                    sorted_values[unique_count++] = sorted_values[i];
                }
            }
        }
        sorted_values = Arrays.copyOf(sorted_values, unique_count);

        int[] compressed_values = new int[array_size];
        for (int value_index = 0; value_index < array_size; ++value_index) {
            long current_value = values[value_index];
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
            compressed_values[value_index] = left;
        }

        int left_index = 0;
        int right_index = 0;

        SlidingKStatistic window =
                new SlidingKStatistic(order_index, compressed_values, sorted_values);
        if (array_size > 0) {
            window.Right(0);
        }

        for (int operation_index = 0; operation_index < operations_count; ++operation_index) {
            char operation_type = operations_string.charAt(operation_index);
            if (operation_type == 'L') {
                window.Left(left_index);
                ++left_index;
            } else if (operation_type == 'R') {
                ++right_index;
                window.Right(right_index);
            }
            output.append(window.GetKth()).append('\n');
        }

        System.out.print(output.toString());
    }
}
