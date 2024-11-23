package com.springboot.diff;

import lombok.Getter;

@Getter
public enum SupportDatabaseTypeEnum {

    MYSQL("mysql", "com.mysql.cj.jdbc.Driver", "CustomMySQLDialect"),

    COCKROACH_DB("cockroachdb", "org.postgresql.Driver", "CustomCockroachdbDialect"),

    SPANNER("spanner", "com.google.cloud.spanner.jdbc.JdbcDriver", "CustomSpannerDialect");

    private String type;
    private String driver;
    private String platform;

    private SupportDatabaseTypeEnum(String type, String driver, String platform) {
        this.type = type;
        this.driver = driver;
        this.platform = platform;
    }

}
