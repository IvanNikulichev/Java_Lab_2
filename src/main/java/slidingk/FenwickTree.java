package slidingk;

public class FenwickTree {
    private int size_;
    private int[] tree_;

    public FenwickTree() {
        this(0);
    }

    public FenwickTree(int size) {
        size_ = size;
        tree_ = new int[size_ + 1];
    }

    public void build(int[] arr) { ... }
    public void update(int index, int delta) { ... }
    public int prefixSum(int index) { ... }
    public int rangeSum(int left, int right) { ... }
    public int findByOrder(int requiredOrder) { ... }

    private void internalAdd(int positionIndex, int delta) { ... }
}
