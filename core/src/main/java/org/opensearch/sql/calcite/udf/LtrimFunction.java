package org.opensearch.sql.calcite.udf;

import java.util.List;
import java.util.Objects;

public class LtrimFunction {
    public static final String FUNCTION_NAME = "ltrim";
    public String eval(Object... args) {
        List<Object> argList = List.of(args);
        String field = argList.get(0).toString();
        int number = Integer.parseInt(argList.get(1).toString());
        return field.substring(0, number);
    }
}