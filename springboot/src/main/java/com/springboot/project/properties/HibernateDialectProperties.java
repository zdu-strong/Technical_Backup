package com.springboot.project.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class HibernateDialectProperties {

    @Value("${spring.jpa.database-platform}")
    private String hibernateDialect;

    public boolean getIsMysql() {
        if (this.hibernateDialect.contains("CustomMySQLDialect")) {
            return true;
        }
        return false;
    }

}
