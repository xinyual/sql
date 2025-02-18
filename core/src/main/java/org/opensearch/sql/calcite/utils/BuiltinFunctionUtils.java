/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.sql.calcite.utils;

import java.util.Collections;
import java.util.Locale;

import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.schema.ScalarFunction;
import org.apache.calcite.schema.impl.ScalarFunctionImpl;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlLibraryOperators;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;
import org.opensearch.sql.calcite.udf.LtrimFunction;
import org.opensearch.sql.calcite.udf.MyIsNotNullFunction;

public interface BuiltinFunctionUtils {

  static SqlOperator translate(String op) {
    switch (op.toUpperCase(Locale.ROOT)) {
      case "AND":
        return SqlStdOperatorTable.AND;
      case "OR":
        return SqlStdOperatorTable.OR;
      case "NOT":
        return SqlStdOperatorTable.NOT;
      case "XOR":
        return SqlStdOperatorTable.BIT_XOR;
      case "=":
        return SqlStdOperatorTable.EQUALS;
      case "<>":
      case "!=":
        return SqlStdOperatorTable.NOT_EQUALS;
      case ">":
        return SqlStdOperatorTable.GREATER_THAN;
      case ">=":
        return SqlStdOperatorTable.GREATER_THAN_OR_EQUAL;
      case "<":
        return SqlStdOperatorTable.LESS_THAN;
      case "<=":
        return SqlStdOperatorTable.LESS_THAN_OR_EQUAL;
      case "+":
        return SqlStdOperatorTable.PLUS;
      case "-":
        return SqlStdOperatorTable.MINUS;
      case "*":
        return SqlStdOperatorTable.MULTIPLY;
      case "/":
        return SqlStdOperatorTable.DIVIDE;
        // Built-in String Functions
      case "LOWER":
        return SqlStdOperatorTable.LOWER;
      case "LIKE":
        return SqlStdOperatorTable.LIKE;
        // Built-in Math Functions
      case "ABS":
        return SqlStdOperatorTable.ABS;
        // Built-in Date Functions
      case "CURRENT_TIMESTAMP":
        return SqlStdOperatorTable.CURRENT_TIMESTAMP;
      case "CURRENT_DATE":
        return SqlStdOperatorTable.CURRENT_DATE;
      case "DATE":
        return SqlLibraryOperators.DATE;
      case "ADDDATE":
        return SqlLibraryOperators.DATE_ADD_SPARK;
      case "DATE_ADD":
        return SqlLibraryOperators.DATEADD;
        // TODO Add more, ref RexImpTable
      case "IS NOT NULL":
        final ScalarFunction udfLengthFunction = ScalarFunctionImpl.create(Types.lookupMethod(MyIsNotNullFunction.class, "eval", String.class));
        SqlIdentifier udfLengthIdentifier = new SqlIdentifier(Collections.singletonList(MyIsNotNullFunction.functionName), null, SqlParserPos.ZERO, null);
        final SqlUserDefinedFunction strLenOperator = new SqlUserDefinedFunction(
                udfLengthIdentifier,
                ReturnTypes.BOOLEAN,
                null,
                null,
                null,
                udfLengthFunction);
        return strLenOperator;
      case "LTRIM":
        final ScalarFunction udfLtrimFunction = ScalarFunctionImpl.create(Types.lookupMethod(LtrimFunction.class, "eval", Object[].class));
        SqlIdentifier udfLtrimIdentifier = new SqlIdentifier(Collections.singletonList(LtrimFunction.FUNCTION_NAME), null, SqlParserPos.ZERO, null);
        final SqlUserDefinedFunction LtrimOperator = new SqlUserDefinedFunction(
                udfLtrimIdentifier,
                SqlKind.OTHER_FUNCTION,
                ReturnTypes.CHAR,
                null,
                null,
                udfLtrimFunction);
        return LtrimOperator;
      default:
        throw new IllegalArgumentException("Unsupported operator: " + op);
    }
  }
}
