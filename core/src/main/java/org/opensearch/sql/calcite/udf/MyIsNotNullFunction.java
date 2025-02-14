package org.opensearch.sql.calcite.udf;

import java.util.Objects;

public class MyIsNotNullFunction {
    public final static String functionName = "ISNOTNULL";
    public Boolean eval(Object a) {
        return !Objects.isNull(a);
    }
}