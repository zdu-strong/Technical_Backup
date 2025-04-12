package com.john.project.common.database;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.MySQLServerConfiguration;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.StandardBasicTypes;

/**
 * In order to use the ifnull method when selecting. In order to use the
 * found_rows method to get the total number of items in group by.
 * 
 * @author zdu
 *
 */
public class CustomMySQLDialect extends MySQLDialect {

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
                "SUBSTRING(DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m-%d %H:%i:%s.%f'), 1, 23)",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND",
                "DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m-%d %H:%i:%s')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE",
                "DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m-%d %H:%i')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR",
                "DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m-%d %H')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY",
                "DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m-%d')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH",
                "DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR", "DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("CONVERT_TO_BIG_DECIMAL", "CAST(?1 AS DECIMAL(65,4))",
                basicTypeRegistry.resolve(StandardBasicTypes.BIG_DECIMAL));
        functionRegistry.registerPattern("CONVERT_TO_STRING", "CAST(?1 AS NCHAR)",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
    }

    public CustomMySQLDialect() {
        super();
    }

    public CustomMySQLDialect(DatabaseVersion version) {
        super(version);
    }

    public CustomMySQLDialect(DatabaseVersion version, int bytesPerCharacter) {
        super(version, bytesPerCharacter);
    }

    public CustomMySQLDialect(DatabaseVersion version, MySQLServerConfiguration serverConfiguration) {
        super(version, serverConfiguration);
    }

    public CustomMySQLDialect(DatabaseVersion version, int bytesPerCharacter, boolean noBackslashEscapes) {
        super(version, bytesPerCharacter, noBackslashEscapes);
    }

    public CustomMySQLDialect(DialectResolutionInfo info) {
        super(info);
    }

}
