package com.springboot.project.enumerate;

import com.springboot.project.properties.HibernateDialectProperties;

import cn.hutool.extra.spring.SpringUtil;

public class EncryptDecryptEnum {

    public static String getId() {
        var isNewSqlDatabase = SpringUtil.getBean(HibernateDialectProperties.class).getIsNewSqlDatabase();
        if (!isNewSqlDatabase) {
            return "1eebc4ea-34b3-64fd-bdc4-47396785f5dd";
        } else {
            return "bb8bb726f916-eb99-f706-5a0e-2f4cbee1";
        }
    }

}
