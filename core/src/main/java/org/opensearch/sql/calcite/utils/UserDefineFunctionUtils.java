package org.opensearch.sql.calcite.utils;

import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.ScalarFunction;
import org.apache.calcite.schema.impl.AggregateFunctionImpl;
import org.apache.calcite.schema.impl.ScalarFunctionImpl;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.validate.SqlUserDefinedAggFunction;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.util.Optionality;
import org.opensearch.sql.calcite.udf.LtrimFunction;
import org.opensearch.sql.calcite.udf.PercentileApproFunction;
import org.opensearch.sql.calcite.udf.UserDefinedAggFunction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.opensearch.sql.expression.function.BuiltinFunctionName.PERCENTILE_APPROX;

public class UserDefineFunctionUtils {
    public static RelBuilder.AggCall TransferUserDefinedAggFunction(
            Class<? extends UserDefinedAggFunction> UDAF,
            String functionName,
            SqlReturnTypeInference returnType,
            List<RexNode> fields,
            List<RexNode> argList,
            RelBuilder relBuilder
    ){
        SqlUserDefinedAggFunction sqlUDAF = new SqlUserDefinedAggFunction(
                new SqlIdentifier(functionName, SqlParserPos.ZERO),
                SqlKind.OTHER_FUNCTION,
                returnType,
                null,
                null,
                AggregateFunctionImpl.create(UDAF),
                false,
                false,
                Optionality.FORBIDDEN
        );
        List<RexNode> addArgList = new ArrayList<>(fields);
        addArgList.addAll(argList);
        return relBuilder.aggregateCall(sqlUDAF, addArgList);
    }

    public static SqlOperator TransferUserDefinedFunction(
        Class<?> UDF,
        String functionName,
        SqlReturnTypeInference returnType
    ){
        final ScalarFunction udfFunction = ScalarFunctionImpl.create(Types.lookupMethod(UDF, "eval", Object[].class));
        SqlIdentifier udfLtrimIdentifier = new SqlIdentifier(Collections.singletonList(functionName), null, SqlParserPos.ZERO, null);
        return new SqlUserDefinedFunction(
                udfLtrimIdentifier,
                SqlKind.OTHER_FUNCTION,
                returnType,
                null,
                null,
                udfFunction);
    }
}
