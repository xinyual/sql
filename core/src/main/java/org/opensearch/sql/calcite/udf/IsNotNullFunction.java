package org.opensearch.sql.calcite.udf;

import org.apache.calcite.sql.*;
import org.apache.calcite.sql.type.*;
import org.apache.calcite.sql.validate.SqlMonotonicity;

public class IsNotNullFunction extends SqlFunction {
    public IsNotNullFunction() {
        super("ISNOTNULL",
                SqlKind.OTHER_FUNCTION,  // It's a function, not a built-in SQL operator
                ReturnTypes.BOOLEAN,     // Returns a BOOLEAN result
                null,
                OperandTypes.ANY,        // Accepts any field type
                SqlFunctionCategory.USER_DEFINED_FUNCTION);
    }

    @Override
    public boolean isDeterministic() {
        return true;  // Function result does not change for the same input
    }

    @Override
    public SqlMonotonicity getMonotonicity(SqlOperatorBinding call) {
        return SqlMonotonicity.CONSTANT;
    }
}