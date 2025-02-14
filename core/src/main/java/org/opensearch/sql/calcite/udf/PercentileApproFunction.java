package org.opensearch.sql.calcite.udf;

import com.tdunning.math.stats.AVLTreeDigest;

public class PercentileApproFunction {

    public static PencentileApproAccumulator init() {
        return new PencentileApproAccumulator();
    }


    // Add values to the accumulator
    public static PencentileApproAccumulator add(PencentileApproAccumulator acc, Object value) {
        acc.add((double) value);
        return acc;
    }

    // Calculate the percentile
    public static Double result(PencentileApproAccumulator acc) {
        if (acc.size() == 0) {
            return null;
        }
        return acc.result();
    }


    public static class PencentileApproAccumulator  extends AVLTreeDigest {
        public static final double DEFAULT_COMPRESSION = 100.0;
        private final double percent;

        public PencentileApproAccumulator() {
            super(DEFAULT_COMPRESSION);
            this.percent = 100;
        }


        public double result(){
            return this.quantile(this.percent);
        }


    }
}
