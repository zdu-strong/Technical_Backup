package com.springboot.diff;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SupportDatabaseTypeEnum {

    MYSQL("mysql", "com.mysql.cj.jdbc.Driver", "CustomMySQLDialect"),

    COCKROACH_DB("cockroachdb", "org.postgresql.Driver", "CustomCockroachdbDialect"),

    SPANNER("spanner", "com.google.cloud.spanner.jdbc.JdbcDriver", "CustomSpannerDialect");

    private String type;
    private String driver;
    private String platform;

}
