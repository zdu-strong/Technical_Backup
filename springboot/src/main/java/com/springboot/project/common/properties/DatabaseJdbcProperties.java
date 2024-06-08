package com.springboot.project.common.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
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
}
