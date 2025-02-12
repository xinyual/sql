package org.opensearch.sql.calcite.udf;

import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;
import org.opensearch.sql.planner.physical.collector.Collector;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class allUDFs {
    public static Map<String, Method> ALLUDFS = Map.of(
            MyUdf1.functionName, Types.lookupMethod(MyUdf1.class, "eval", String.class)
    );
}