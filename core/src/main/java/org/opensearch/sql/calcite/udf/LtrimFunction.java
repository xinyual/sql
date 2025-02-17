package org.opensearch.sql.calcite.udf;

import java.util.Objects;

public class LtrimFunction {
    public static final String FUNCTION_NAME = "ltrim";
    public String eval(String field, int number) {
        return field.substring(0, number);
    }
}