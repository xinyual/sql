/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.sql.calcite.utils;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.rel.RelCollations;
import org.apache.calcite.rel.core.AggregateCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.impl.AggregateFunctionImpl;
import org.apache.calcite.sql.SqlAggFunction;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.validate.SqlUserDefinedAggFunction;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.util.Optionality;
import org.opensearch.sql.ast.expression.AggregateFunction;
import org.opensearch.sql.calcite.CalcitePlanContext;
import org.opensearch.sql.calcite.udf.PercentileApproFunction;
import org.opensearch.sql.expression.function.BuiltinFunctionName;

import java.util.List;

import static org.opensearch.sql.calcite.utils.UserDefineFunctionUtils.TransferUserDefinedAggFunction;
import static org.opensearch.sql.expression.function.BuiltinFunctionName.PERCENTILE_APPROX;

public interface AggregateUtils {
  static RelBuilder.AggCall translate(
      AggregateFunction agg, RexNode field, CalcitePlanContext context, List<RexNode> argList) {
    if (BuiltinFunctionName.ofAggregation(agg.getFuncName()).isEmpty())
      throw new IllegalStateException("Unexpected value: " + agg.getFuncName());

    // Additional aggregation function operators will be added here
    BuiltinFunctionName functionName = BuiltinFunctionName.ofAggregation(agg.getFuncName()).get();
    switch (functionName) {
      case MAX:
        return context.relBuilder.max(field);
      case PERCENTILE_APPROX:
        /*
        SqlUserDefinedAggFunction percentileApproUDAF = new SqlUserDefinedAggFunction(
                new SqlIdentifier(String.valueOf(PERCENTILE_APPROX), SqlParserPos.ZERO),
                SqlKind.OTHER_FUNCTION,
                ReturnTypes.DOUBLE, // or your specific return type inference
                null, // or your specific operand type inference
                null, // operand metadata
                AggregateFunctionImpl.create(PercentileApproFunction.class),
                false, // requiresOrder
                false, // requiresOver
                Optionality.FORBIDDEN // requiresGroupOrder
        );
         */
        return TransferUserDefinedAggFunction(PercentileApproFunction.class, String.valueOf(PERCENTILE_APPROX), ReturnTypes.DOUBLE, List.of(field), argList, context.relBuilder);
        //return context.relBuilder.aggregateCall(percentileApproUDAF, List.of(field, argList.getFirst()));
      case AVG:
        return context.relBuilder.avg(agg.getDistinct(), null, field);
      case COUNT:
        return context.relBuilder.count(
            agg.getDistinct(), null, field == null ? ImmutableList.of() : ImmutableList.of(field));
      case SUM:
        return context.relBuilder.sum(agg.getDistinct(), null, field);
      case STDDEV_POP:
        return context.relBuilder.aggregateCall(SqlStdOperatorTable.STDDEV_POP, field);
      case STDDEV_SAMP:
        return context.relBuilder.aggregateCall(SqlStdOperatorTable.STDDEV_SAMP, field);
      case MIN:
        return context.relBuilder.min(field);
    }
    throw new IllegalStateException("Not Supported value: " + agg.getFuncName());
  }

  static AggregateCall aggCreate(SqlAggFunction agg, boolean isDistinct, RexNode field) {
    int index = ((RexInputRef) field).getIndex();
    return AggregateCall.create(
        agg,
        isDistinct,
        false,
        false,
        ImmutableList.of(),
        ImmutableList.of(index),
        -1,
        null,
        RelCollations.EMPTY,
        field.getType(),
        null);
  }
}
