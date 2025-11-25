package slidingk;

public class SlidingKStatistic {
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

    public void Right(int array_position) { ... }
    public void Left(int array_position) { ... }
    public long GetKth() { ... }
}
