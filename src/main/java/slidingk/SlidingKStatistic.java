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
        this.order_index_ = orderIndex;
        this.window_size_ = 0;
        this.compressed_values_ = compressedValues;
        this.sorted_values_ = sortedValues;
        this.fenwick_ = new FenwickTree(sortedValues.length);
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
