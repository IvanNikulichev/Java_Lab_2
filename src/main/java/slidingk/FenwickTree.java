package slidingk;

public class FenwickTree {
    private int size;
    private int[] tree;

    public FenwickTree() {
        this(0);
    }

    public FenwickTree(int size) {
        this.size = size;
        this.tree = new int[size + 1];
    }

    public void build(int[] arr) {
        size = arr.length;
        tree = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            internalAdd(i, arr[i]);
        }
    }

    public void update(int index, int delta) {
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
        while (position > 0) {
            result += tree[position];
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
        int sumRight = prefixSum(right);
        int sumLeft = (left > 0) ? prefixSum(left - 1) : 0;
        return sumRight - sumLeft;
    }

    public int findByOrder(int requiredOrder) {
        int currentIndex = 0;
        int bitMask = 1;
        while ((bitMask << 1) <= size) {
            bitMask <<= 1;
        }
        int remainingOrder = requiredOrder;
        for (int step = bitMask; step > 0; step >>= 1) {
            int nextIndex = currentIndex + step;
            if (nextIndex <= size && tree[nextIndex] < remainingOrder) {
                remainingOrder -= tree[nextIndex];
                currentIndex = nextIndex;
            }
        }
        return currentIndex;
    }

    private void internalAdd(int positionIndex, int delta) {
        int position = positionIndex + 1;
        while (position <= size) {
            tree[position] += delta;
            position += position & -position;
        }
    }
}
