package com.springboot.project.common.mysql;

import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardJDBCEscapeFunction;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.dialect.H2Dialect;

/**
 * In order to use the ifnull method when selecting
 * 
 * @author zdu
 *
 */
public class CustomH2Dialect extends H2Dialect {
    public CustomH2Dialect() {
        super();
        registerFunction("IFNULL", new StandardJDBCEscapeFunction("IFNULL", StandardBasicTypes.LONG));
        var maxRecursionLevel = 10;
        var isChildStringBuilder = new StringBuilder();
        isChildStringBuilder.append("(");
        isChildStringBuilder.append("exists (");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("select organize.id");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("from organize_entity organize");
        isChildStringBuilder.append(" ");
        for (int i = 2; i <= maxRecursionLevel; i++) {
            var firstTableName = "organize" + (i == 2 ? "" : "_" + (i - 1));
            var secondTableName = "organize_" + i;
            isChildStringBuilder.append("left join  organize_entity " + secondTableName);
            isChildStringBuilder.append(" ");
            isChildStringBuilder.append("on " + firstTableName + ".parent_organize_id = " + secondTableName + ".id");
            isChildStringBuilder.append(" ");
        }
        isChildStringBuilder.append("where organize.id = ?1");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("and");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("?2");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("in");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("(");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("organize.parent_organize_id");
        isChildStringBuilder.append(" ");
        for (int i = 2; i <= maxRecursionLevel; i++) {
            var secondTableName = "organize_" + i;
            isChildStringBuilder.append(",");
            isChildStringBuilder.append(" ");
            isChildStringBuilder.append(secondTableName + ".parent_organize_id");
            isChildStringBuilder.append(" ");
        }
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append(")");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append(")");
        isChildStringBuilder.append(")");
        registerFunction("IS_CHILD_ORGANIZE",
                new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, isChildStringBuilder.toString()));
        registerFunction("IS_SORT_AT_BEFORE", new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "(?1 < ?2)"));
    }
}