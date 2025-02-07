package com.springboot.project.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseJdbcProperties {

    @Autowired
    private HibernateDialectProperties hibernateDialectProperties;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    public boolean getIsSpannerEmulator() {
        if (this.hibernateDialectProperties.getIsSpanner()) {
            if (this.jdbcUrl.contains("autoConfigEmulator=true")) {
                return true;
            }
        }
        return false;
    }

    public boolean getIsSupportParallelWrite() {
        return this.getIsSpannerEmulator() || this.hibernateDialectProperties.getIsH2();
    }

    public boolean getIsNewSqlDatabase() {
        return this.hibernateDialectProperties.getIsSpanner() || this.hibernateDialectProperties.getIsCockroachDB();
    }
}
