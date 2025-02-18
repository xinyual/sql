package org.opensearch.sql.calcite.udf;

import org.apache.calcite.rex.RexNode;

import java.util.List;

public abstract class UserDefinedAggFunction<S extends Accumulator> {
    public abstract S init();
    public abstract Object result(S accumulator);

    // Add values to the accumulator
    public abstract S add(S acc, Object... values);
}
