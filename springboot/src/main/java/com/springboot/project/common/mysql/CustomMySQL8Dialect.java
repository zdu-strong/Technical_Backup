package com.springboot.project.common.mysql;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardJDBCEscapeFunction;
import org.hibernate.type.StandardBasicTypes;

/**
 * In order to use the ifnull method when selecting. In order to use the
 * found_rows method to get the total number of items in group by.
 * 
 * @author zdu
 *
 */
public class CustomMySQL8Dialect extends MySQL8Dialect {
    public CustomMySQL8Dialect() {
        super();
        registerFunction("IFNULL", new StandardJDBCEscapeFunction("IFNULL", StandardBasicTypes.LONG));
        registerFunction("FOUND_ROWS", new NoArgSQLFunction("SQL_CALC_FOUND_ROWS FOUND_ROWS", StandardBasicTypes.LONG));
        var isChildStringBuilder = new StringBuilder();
        isChildStringBuilder.append("?2");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("in");
        isChildStringBuilder.append("(");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("with recursive cte as (");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("select id");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("from organize_entity");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("where id = ?1");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("union all");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("select organize.parent_organize_id");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("from cte inner join organize_entity organize");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("on cte.id = organize.id");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append(")");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("select * from cte");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append(")");
        registerFunction("IS_CHILD_ORGANIZE",
                new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, isChildStringBuilder.toString()));
        registerFunction("IS_SORT_AT_BEFORE", new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "(?1 < ?2)"));
    }
}
