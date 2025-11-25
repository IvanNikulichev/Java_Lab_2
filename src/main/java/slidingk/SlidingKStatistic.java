package slidingk;

public class SlidingKStatistic {
    // k-th order statistic (1-based index inside the window)
    private final int order_index_;

    // current window size
    private int window_size_;

    // compressed value index for each position in the original array
    private final int[] compressed_values_;

    // sorted array of unique values (by these indexes we work with FenwickTree)
    private final long[] sorted_values_;

    // Fenwick tree that stores frequencies of values in the current window
    private final FenwickTree fenwick_;

    public SlidingKStatistic(int orderIndex,
                             int[] compressedValues,
                             long[] sortedValues) {
        if (orderIndex <= 0) {
            throw new IllegalArgumentException("orderIndex must be positive");
        }
        if (compressedValues == null || sortedValues == null) {
            throw new IllegalArgumentException("Arrays must not be null");
        }

        this.order_index_ = orderIndex;
        this.window_size_ = 0;
        this.compressed_values_ = compressedValues;
        this.sorted_values_ = sortedValues;
        this.fenwick_ = new FenwickTree(sortedValues.length);
    }

    // Move the right border of the window to the right:
    // we add a new element with index array_position into the window.
    public void Right(int array_position) {
        if (array_position < 0 || array_position >= compressed_values_.length) {
            throw new IndexOutOfBoundsException(
                    "array_position out of range: " + array_position
            );
        }

        // Get compressed index of the value at this position
        int value_index = compressed_values_[array_position];

        // Increase frequency of this value in Fenwick tree
        fenwick_.update(value_index, 1);

        // Window size grows by one
        ++window_size_;
    }

    // Move the left border of the window to the right:
    // we remove the element with index array_position from the window.
    public void Left(int array_position) {
        if (window_size_ == 0) {
            throw new IllegalStateException("Cannot shrink empty window");
        }
        if (array_position < 0 || array_position >= compressed_values_.length) {
            throw new IndexOutOfBoundsException(
                    "array_position out of range: " + array_position
            );
        }

        // Get compressed index of the value at this position
        int value_index = compressed_values_[array_position];

        // Decrease frequency of this value in Fenwick tree
        fenwick_.update(value_index, -1);

        // Window size becomes smaller
        --window_size_;
    }

    // Return k-th smallest value in the current window.
    // If the window is too small (less than k elements), return -1.
    public long GetKth() {
        // Not enough elements to get k-th statistic
        if (window_size_ < order_index_) {
            return -1L;
        }
        if (sorted_values_.length == 0) {
            return -1L;
        }

        // Find index of the value by its "order" using Fenwick tree.
        // order_index_ is the k in "k-th smallest".
        int value_index = fenwick_.findByOrder(order_index_);

        // Map compressed index back to the real value.
        return sorted_values_[value_index];
    }
}
