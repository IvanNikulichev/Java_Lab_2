package slidingk;

public class FenwickTree {
    private int size;
    private int[] tree;

    public FenwickTree() {
        this(0);
    }

    public FenwickTree(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size must be non-negative");
        }
        this.size = size;
        this.tree = new int[size + 1];
    }

    public void build(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("Input array must not be null");
        }
        size = arr.length;
        tree = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            internalAdd(i, arr[i]);
        }
    }

    public void update(int index, int delta) {
        if (size == 0) {
            throw new IllegalStateException("Cannot update empty Fenwick tree");
        }
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
        internalAdd(index, delta);
    }

    public int prefixSum(int index) {
        if (index < 0) {
            return 0;
        }
        if (size == 0) {
            return 0;
        }
        if (index >= size) {
            index = size - 1;
        }

        int result = 0;
        int position = index + 1;
        // Move up the Fenwick tree.
        // Each step we add the value of a node that covers some prefix.
        while (position > 0) {
            result += tree[position];
            // Remove lowest set bit to jump to the parent node.
            position -= position & -position;
        }
        return result;
    }

    public int rangeSum(int left, int right) {
        if (left > right) {
            return 0;
        }
        if (size == 0) {
            return 0;
        }
        if (right < 0) {
            return 0;
        }
        if (left < 0) {
            left = 0;
        }
        if (right >= size) {
            right = size - 1;
        }

        // Sum on [left, right] = prefix(right) - prefix(left - 1).
        // This works because prefixSum(i) is sum on [0, i].
        int sumRight = prefixSum(right);
        int sumLeft = (left > 0) ? prefixSum(left - 1) : 0;
        return sumRight - sumLeft;
    }

    public int findByOrder(int requiredOrder) {
        if (size == 0) {
            throw new IllegalStateException("Cannot search order statistic in empty Fenwick tree");
        }
        if (requiredOrder <= 0) {
            throw new IllegalArgumentException("Order must be positive");
        }

        int total = prefixSum(size - 1);
        if (requiredOrder > total) {
            throw new IllegalArgumentException(
                    "Order is greater than total frequency: " + requiredOrder + " > " + total
            );
        }

        int currentIndex = 0;

        // Find the largest power of two <= size.
        // We will use it as the highest step for binary search on the tree.
        int bitMask = 1;
        while ((bitMask << 1) <= size) {
            bitMask <<= 1;
        }

        int remainingOrder = requiredOrder;

        // Binary search on Fenwick tree:
        // try to move to the right while the prefix sum stays < requiredOrder.
        for (int step = bitMask; step > 0; step >>= 1) {
            int nextIndex = currentIndex + step;
            // If we can jump to nextIndex and the sum is still too small,
            // we go there and decrease the remaining order.
            if (nextIndex <= size && tree[nextIndex] < remainingOrder) {
                remainingOrder -= tree[nextIndex];
                currentIndex = nextIndex;
            }
        }

        // currentIndex is the last position where prefix sum < requiredOrder.
        // The element with this order has index currentIndex (0-based).
        return currentIndex;
    }

    private void internalAdd(int positionIndex, int delta) {
        int position = positionIndex + 1;

        // Go through all nodes that cover this index.
        // position & -position gives the lowest set bit of position.
        // Adding it moves us to the next responsible node.
        while (position <= size) {
            tree[position] += delta;
            position += position & -position;
        }
    }
}
