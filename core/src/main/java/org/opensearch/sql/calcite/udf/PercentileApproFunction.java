package org.opensearch.sql.calcite.udf;

import com.tdunning.math.stats.AVLTreeDigest;

public class PercentileApproFunction {

    public static PencentileApproAccumulator init(double percent) {
        return new PencentileApproAccumulator(percent);
    }

    public static PencentileApproAccumulator init(double compression, double percent) {
        return new PencentileApproAccumulator(compression, percent);
    }

    // Add values to the accumulator
    public static PencentileApproAccumulator add(PencentileApproAccumulator acc, double value) {
        acc.add(value);
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

        public PencentileApproAccumulator(double percent) {
            super(DEFAULT_COMPRESSION);
            if (percent < 0.0 || percent > 100.0) {
                throw new IllegalArgumentException("out of bounds percent value, must be in [0, 100]");
            }
            this.percent = percent / 100.0;
        }

        public PencentileApproAccumulator(double compression, double percent) {
            super(compression);
            if (percent < 0.0 || percent > 100.0) {
                throw new IllegalArgumentException("out of bounds percent value, must be in [0, 100]");
            }
            this.percent = percent / 100.0;
        }

        public double result(){
            return this.quantile(this.percent);
        }


    }
}
