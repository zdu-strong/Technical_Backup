package com.springboot.project.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HibernateDialectProperties {

    @Value("${spring.jpa.database-platform}")
    private String hibernateDialect;

    public boolean getIsCockroachDB() {
        if (this.hibernateDialect.contains("CustomCockroachdbDialect")) {
            return true;
        }
        return false;
    }

    public boolean getIsSpanner() {
        if (this.hibernateDialect.contains("CustomSpannerDialect")) {
            return true;
        }
        return false;
    }

    public boolean getIsMysql() {
        if (this.hibernateDialect.contains("CustomMySQLDialect")) {
            return true;
        }
        return false;
    }

    public boolean getIsH2() {
        if (this.hibernateDialect.contains("CustomH2Dialect")) {
            return true;
        }
        return false;
    }

}
