package org.opensearch.sql.calcite.udf;

import java.util.Objects;

public class MyUdf1 {
    public final static String functionName = "ISNOTNULL";
    public Boolean eval(Object a) {
        return Objects.isNull(a);
    }
}