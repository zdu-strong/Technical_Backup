package com.springboot.project.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class HibernateDialectProperties {

    @Value("${spring.jpa.database-platform}")
    private String hibernateDialect;

    public boolean getIsSpanner() {
        if (this.hibernateDialect.contains("CustomSpannerDialect")) {
            return true;
        }
        return false;
    }

    public boolean getIsNewSqlDatabase() {
        return !this.getIsMysql() && !this.getIsH2();
    }

    private boolean getIsMysql() {
        if (this.hibernateDialect.contains("CustomMySQLDialect")) {
            return true;
        }
        return false;
    }

    private boolean getIsH2() {
        if (this.hibernateDialect.contains("CustomH2Dialect")) {
            return true;
        }
        return false;
    }

}
