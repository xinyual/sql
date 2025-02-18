package org.opensearch.sql.calcite.udf;

import java.util.List;
import java.util.Objects;

public class LtrimFunction extends UserDefinedFunction {
    @Override
    public Object eval(Object... args) {
        List<Object> argList = List.of(args);
        String targetString = argList.get(0).toString();
        int trimNumber = (int) argList.get(1);
        return targetString.substring(0, trimNumber);
    }
}