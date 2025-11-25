package slidingk;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class SlidingKStatisticTest {

    private static class TestWindow {
        final SlidingKStatistic window;
        final int[] compressed;
        final long[] sorted;

        TestWindow(SlidingKStatistic window, int[] compressed, long[] sorted) {
            this.window = window;
            this.compressed = compressed;
            this.sorted = sorted;
        }
    }

    private static TestWindow makeWindow(long[] values, int k) {
        long[] sorted = values.clone();
        Arrays.sort(sorted);

        int uniqueCount = 0;
        if (sorted.length > 0) {
            uniqueCount = 1;
            for (int i = 1; i < sorted.length; ++i) {
                if (sorted[i] != sorted[i - 1]) {
                    sorted[uniqueCount++] = sorted[i];
                }
            }
        }
        sorted = Arrays.copyOf(sorted, uniqueCount);

        int[] compressed = new int[values.length];
        for (int i = 0; i < values.length; ++i) {
            long v = values[i];
            int left = 0;
            int right = sorted.length - 1;
            while (left < right) {
                int mid = (left + right) >>> 1;
                if (sorted[mid] < v) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }
            compressed[i] = left;
        }

        SlidingKStatistic w = new SlidingKStatistic(k, compressed, sorted);
        return new TestWindow(w, compressed, sorted);
    }

    private static long[] runOperations(long[] values, int k, String ops) {
        TestWindow tw = makeWindow(values, k);
        SlidingKStatistic window = tw.window;

        int n = values.length;
        int leftIndex = 0;
        int rightIndex = 0;

        long[] answer = new long[ops.length()];

        if (n > 0) {
            window.Right(0);
        }

        for (int i = 0; i < ops.length(); ++i) {
            char op = ops.charAt(i);
            if (op == 'L') {
                window.Left(leftIndex);
                ++leftIndex;
            } else if (op == 'R') {
                ++rightIndex;
                window.Right(rightIndex);
            }
            answer[i] = window.GetKth();
        }
        return answer;
    }

    @Test
    void testSampleRRLL() {
        long[] values = {4, 2, 1, 3, 6, 5, 7};
        int k = 2;
        String ops = "RRLL";

        long[] res = runOperations(values, k, ops);

        assertArrayEquals(new long[]{4, 2, 2, -1}, res);
    }

    @Test
    void testKEqualsOneMinimalElement() {
        long[] values = {5, 1, 4, 3, 2};
        int k = 1;

        long[] res = runOperations(values, k, "RRRR"); // окно растёт вправо

        // окна: [5,1], [5,1,4], [5,1,4,3], [5,1,4,3,2] -> минимумы: 1,1,1,1
        assertArrayEquals(new long[]{1, 1, 1, 1}, res);
    }

    @Test
    void testKEqualsWindowSizeMaxElement() {
        long[] values = {2, 7, 3, 9};
        int k = 2;

        long[] res = runOperations(values, k, "RR"); // окна: [2,7], [2,7,3]

        // [2,7] -> отсортированно [2,7], k=2 -> 7
        // [2,7,3] -> [2,3,7], k=2 -> 3
        assertArrayEquals(new long[]{7, 3}, res);
    }

    @Test
    void testWithDuplicates() {
        long[] values = {5, 5, 5, 5};
        int k = 2;

        long[] res = runOperations(values, k, "RRR");

        // окна:
        // [5,5]      -> {5,5}, 2-я статистика = 5
        // [5,5,5]    -> {5,5,5}, 2-я = 5
        // [5,5,5,5]  -> {5,5,5,5}, 2-я = 5
        assertArrayEquals(new long[]{5, 5, 5}, res);
    }

    @Test
    void testMoveWindowLeftAndRight() {
        long[] values = {1, 4, 2, 6, 3};
        int k = 2;

        // послед-ть операций:
        // start: [1]
        // R: [1,4]
        // R: [1,4,2]
        // L: [4,2]
        // R: [4,2,6]
        String ops = "RRLR";

        long[] res = runOperations(values, k, ops);

        // окна:
        // [1,4]      -> [1,4],    2-я = 4
        // [1,4,2]    -> [1,2,4],  2-я = 2
        // [4,2]      -> [2,4],    2-я = 4
        // [4,2,6]    -> [2,4,6],  2-я = 4
        assertArrayEquals(new long[]{4, 2, 4, 4}, res);
    }

    @Test
    void testKTooLargeAlwaysMinusOne() {
        long[] values = {10, 20, 30};
        int k = 5;
        String ops = "RRR";

        long[] res = runOperations(values, k, ops);

        assertArrayEquals(new long[]{-1, -1, -1}, res);
    }
}
