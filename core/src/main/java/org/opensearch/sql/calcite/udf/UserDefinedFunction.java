package org.opensearch.sql.calcite.udf;

public abstract class UserDefinedFunction  {
    public abstract Object eval(Object... args);
}
