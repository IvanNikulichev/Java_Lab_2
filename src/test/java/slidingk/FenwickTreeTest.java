package slidingk;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FenwickTreeTest {

    @Test
    void testPrefixAndRangeSumSimple() {
        FenwickTree tree = new FenwickTree();
        int[] arr = {1, 2, 3, 4};
        tree.build(arr);

        assertEquals(1, tree.prefixSum(0));
        assertEquals(1 + 2 + 3 + 4, tree.prefixSum(3));
        assertEquals(2 + 3, tree.rangeSum(1, 2));
    }

    @Test
    void testUpdate() {
        FenwickTree tree = new FenwickTree();
        int[] arr = {1, 2, 3, 4};
        tree.build(arr);

        tree.update(1, 3); // arr[1] += 3 → 5

        assertEquals(1 + 5 + 3 + 4, tree.prefixSum(3));
        assertEquals(5 + 3, tree.rangeSum(1, 2));
    }

    @Test
    void testFindByOrderWithDuplicates() {
        FenwickTree tree = new FenwickTree(3);
        tree.update(0, 2);
        tree.update(1, 1);
        tree.update(2, 3);

        assertEquals(0, tree.findByOrder(1));
        assertEquals(0, tree.findByOrder(2));
        assertEquals(1, tree.findByOrder(3));
        assertEquals(2, tree.findByOrder(4));
    }
}
