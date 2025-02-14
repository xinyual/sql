package org.opensearch.sql.calcite.udf;

import com.tdunning.math.stats.AVLTreeDigest;

public class PercentileApproFunction {

    public static PencentileApproAccumulator init() {
        return new PencentileApproAccumulator();
    }


    // Add values to the accumulator
    public static PencentileApproAccumulator add(PencentileApproAccumulator acc, Object value, Object percent) {
        acc.add((float) value, (int) percent);
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
        private double percent;

        public PencentileApproAccumulator() {
            super(DEFAULT_COMPRESSION);
            this.percent = 1.0;
        }

        public void add(float value, int percent) {
            this.percent = percent / 100.0;
            this.add(value);
        }


        public double result(){
            return this.quantile(this.percent);
        }


    }
}
