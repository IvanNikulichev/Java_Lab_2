package slidingk;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FenwickTreeTest {

    @Test
    void testBuildAndPrefixSumSimple() {
        FenwickTree tree = new FenwickTree();
        int[] arr = {1, 2, 3, 4};
        tree.build(arr);

        assertEquals(1, tree.prefixSum(0));
        assertEquals(1 + 2, tree.prefixSum(1));
        assertEquals(1 + 2 + 3, tree.prefixSum(2));
        assertEquals(1 + 2 + 3 + 4, tree.prefixSum(3));
    }

    @Test
    void testRangeSumSimple() {
        FenwickTree tree = new FenwickTree();
        int[] arr = {1, 2, 3, 4};
        tree.build(arr);

        assertEquals(1, tree.rangeSum(0, 0));
        assertEquals(2, tree.rangeSum(1, 1));
        assertEquals(2 + 3, tree.rangeSum(1, 2));
        assertEquals(1 + 2 + 3 + 4, tree.rangeSum(0, 3));
    }

    @Test
    void testUpdateSingleAndMultiple() {
        FenwickTree tree = new FenwickTree();
        int[] arr = {1, 2, 3, 4, 5};
        tree.build(arr);

        // один апдейт
        tree.update(2, 5); // arr[2] = 3 + 5 = 8
        assertEquals(1 + 2 + 8, tree.prefixSum(2));
        assertEquals(8 + 4, tree.rangeSum(2, 3));

        // несколько апдейтов подряд
        tree.update(0, -1); // arr[0] = 0
        tree.update(4, 2);  // arr[4] = 7

        // итоговый массив: [0, 2, 8, 4, 7]
        assertEquals(0, tree.rangeSum(0, 0));
        assertEquals(0 + 2, tree.prefixSum(1));
        assertEquals(0 + 2 + 8 + 4 + 7, tree.prefixSum(4));
    }

    @Test
    void testPrefixAndRangeOutOfBounds() {
        FenwickTree tree = new FenwickTree();
        int[] arr = {5, 10, 15};
        tree.build(arr);

        // prefixSum с индексом меньше 0 и больше размера
        assertEquals(0, tree.prefixSum(-1));
        assertEquals(5 + 10 + 15, tree.prefixSum(100));

        // rangeSum с разными "кривыми" границами
        assertEquals(0, tree.rangeSum(5, 1));     // left > right
        assertEquals(0, tree.rangeSum(-5, -1));   // правый < 0
        assertEquals(5 + 10 + 15, tree.rangeSum(-10, 100));
        assertEquals(10 + 15, tree.rangeSum(1, 100));
    }

    @Test
    void testEmptyTreeBehavior() {
        FenwickTree tree = new FenwickTree();
        int[] empty = {};
        tree.build(empty);

        assertEquals(0, tree.prefixSum(0));
        assertEquals(0, tree.rangeSum(0, 0));
    }

    @Test
    void testFindByOrderWithDuplicates() {
        FenwickTree tree = new FenwickTree(3);
        // частоты: [2, 1, 3]
        tree.update(0, 2);
        tree.update(1, 1);
        tree.update(2, 3);

        // порядок: индексы [0,0,1,2,2,2]
        assertEquals(0, tree.findByOrder(1));
        assertEquals(0, tree.findByOrder(2));
        assertEquals(1, tree.findByOrder(3));
        assertEquals(2, tree.findByOrder(4));
        assertEquals(2, tree.findByOrder(5));
        assertEquals(2, tree.findByOrder(6));
    }

    @Test
    void testMultipleFindByOrderAfterUpdates() {
        FenwickTree tree = new FenwickTree(4);
        // частоты: [1,1,1,1]
        for (int i = 0; i < 4; ++i) {
            tree.update(i, 1);
        }

        assertEquals(0, tree.findByOrder(1));
        assertEquals(1, tree.findByOrder(2));
        assertEquals(2, tree.findByOrder(3));
        assertEquals(3, tree.findByOrder(4));

        // перекачиваем счётчики: всё к индексу 2
        tree.update(0, -1);
        tree.update(1, -1);
        tree.update(3, -1);
        tree.update(2, 3);

        // частоты: [0,0,4,0] => порядок [2,2,2,2]
        assertEquals(2, tree.findByOrder(1));
        assertEquals(2, tree.findByOrder(2));
        assertEquals(2, tree.findByOrder(3));
        assertEquals(2, tree.findByOrder(4));
    }
}
