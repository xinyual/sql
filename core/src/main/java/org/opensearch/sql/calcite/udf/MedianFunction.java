package org.opensearch.sql.calcite.udf;


import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.util.ImmutableIntList;
import org.apache.calcite.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedianFunction {
    // Accumulator class to store values
    public static class MedianAccumulator {
        public final List<Long> values = new ArrayList<>();
    }

    // Initialize the accumulator
    public static MedianAccumulator init() {
        return new MedianAccumulator();
    }

    // Add values to the accumulator
    public static MedianAccumulator add(MedianAccumulator acc, Long value) {
        if (value != null) {
            acc.values.add(value);
        }
        return acc;
    }

    // Calculate the median
    public static Long result(MedianAccumulator acc) {
        if (acc.values.isEmpty()) {
            return null;
        }

        List<Long> sorted = new ArrayList<>(acc.values);
        Collections.sort(sorted);

        int size = sorted.size();
        if (size % 2 == 0) {
            return (sorted.get(size/2 - 1) + sorted.get(size/2)) / 2;
        } else {
            return sorted.get(size/2);
        }
    }
}