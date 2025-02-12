package org.opensearch.sql.calcite.udf;

import org.apache.calcite.rex.*;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.type.SqlTypeName;

public class IsNotNullRexCall extends RexCall {
    public IsNotNullRexCall(RexNode operand) {
        super(operand.getType(), new IsNotNullFunction(),
                java.util.Collections.singletonList(operand));
    }

    @Override
    public SqlKind getKind() {
        return SqlKind.OTHER_FUNCTION;  // Custom function type
    }

    @Override
    public boolean isAlwaysTrue() {
        return false;
    }

    // **NULL CHECK: Evaluate dynamically during execution**
    public boolean evaluate(Object value) {
        return value != null;
    }
}
