package org.opensearch.sql.calcite.udf;

import org.apache.calcite.rex.RexNode;

import java.util.List;

public interface UserDefinedAggFunction<S extends Accumulator> {
    S init();
    Object result(S accumulator);

    // Add values to the accumulator
    S add(S acc, Object... values);
}