package com.springboot.project.common.database;

import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.H2Dialect;

/**
 * In order to use the ifnull method when selecting
 * 
 * @author zdu
 *
 */
public class CustomH2Dialect extends H2Dialect {

    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);
        BasicTypeRegistry basicTypeRegistry = functionContributions.getTypeConfiguration().getBasicTypeRegistry();
        SqmFunctionRegistry functionRegistry = functionContributions.getFunctionRegistry();
        functionRegistry.register("IFNULL", new StandardSQLFunction("IFNULL", StandardBasicTypes.BIG_DECIMAL));
        functionRegistry.registerPattern("FOUND_ROWS", "COUNT(*) OVER()",
                basicTypeRegistry.resolve(StandardBasicTypes.LONG));
        functionRegistry.registerPattern("LOCATE", "LOCATE(?1, ?2)",
                basicTypeRegistry.resolve(StandardBasicTypes.LONG));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILLISECOND",
                "TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM-DD HH24:mi:ss.ff3')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND",
                "TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM-DD HH24:mi:ss')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE",
                "TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM-DD HH24:mi')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR",
                "TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM-DD HH24')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY",
                "TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM-DD')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH",
                "TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR",
                "TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("CONVERT_TO_BIG_DECIMAL", "CAST(?1 AS NUMERIC(65, 4))",
                basicTypeRegistry.resolve(StandardBasicTypes.BIG_DECIMAL));
        functionRegistry.registerPattern("CONVERT_TO_STRING", "CAST(?1 AS CHARACTER VARYING)",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
    }

    public CustomH2Dialect() {
        super();
    }

    public CustomH2Dialect(DatabaseVersion version) {
        super(version);
    }

    public CustomH2Dialect(DialectResolutionInfo info) {
        super(info);
    }
}