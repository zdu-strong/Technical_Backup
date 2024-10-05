package com.springboot.project.common.database;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import org.jinq.jpa.JinqJPAStreamProvider;

/**
 * In order to call database-specific functions
 * 
 * @author zdu
 *
 */
public class JPQLFunction {
    /**
     * It accepts two parameters, and if the first parameter is not NULL, it returns
     * the first parameter. Otherwise, the IFNULL function returns the second
     * parameter.
     * 
     * @param value
     * @param defaultValue
     * @return
     */
    public static BigDecimal ifnull(Long value, int defaultValue) {
        throw new RuntimeException();
    }

    /**
     * It accepts two parameters, and if the first parameter is not NULL, it returns
     * the first parameter. Otherwise, the IFNULL function returns the second
     * parameter.
     * 
     * @param value
     * @param defaultValue
     * @return
     */
    public static BigDecimal ifnull(Integer value, int defaultValue) {
        throw new RuntimeException();
    }

    /**
     * It accepts two parameters, and if the first parameter is not NULL, it returns
     * the first parameter. Otherwise, the IFNULL function returns the second
     * parameter.
     * 
     * @param value
     * @param defaultValue
     * @return
     */
    public static BigDecimal ifnull(BigDecimal value, int defaultValue) {
        throw new RuntimeException();
    }

    /**
     * In order to obtain the total number of entries during paging, compatibility
     * processing has been done at the place of call. Please do not call it.
     * 
     * @return
     */
    public static Long foundTotalRowsForGroupBy() {
        throw new RuntimeException();
    }

    /**
     * format date to string
     * 
     * @param utcOffset like +08:00
     * @return like 2022
     */
    public static String formatDateAsYear(Date date, String utcOffset) {
        throw new RuntimeException();
    }

    /**
     * format date to string
     * 
     * @param utcOffset like +08:00
     * @return like 2022-08
     */
    public static String formatDateAsYearMonth(Date date, String utcOffset) {
        throw new RuntimeException();
    }

    /**
     * format date to string
     * 
     * @param utcOffset like +08:00
     * @return like 2022-08-08
     */
    public static String formatDateAsYearMonthDay(Date date, String utcOffset) {
        throw new RuntimeException();
    }

    /**
     * format date to string
     * 
     * @param utcOffset like +08:00
     * @return like 2022-08-08 13
     */
    public static String formatDateAsYearMonthDayHour(Date date, String utcOffset) {
        throw new RuntimeException();
    }

    /**
     * format date to string
     * 
     * @param utcOffset like +08:00
     * @return like 2022-08-08 13:05
     */
    public static String formatDateAsYearMonthDayHourMinute(Date date, String utcOffset) {
        throw new RuntimeException();
    }

    /**
     * format date to string
     * 
     * @param utcOffset like +08:00
     * @return like 2022-08-08 13:05:06
     */
    public static String formatDateAsYearMonthDayHourMinuteSecond(Date date, String utcOffset) {
        throw new RuntimeException();
    }

    /**
     * format date to string
     * 
     * @param utcOffset like +08:00
     * @return like 2022-08-08 13:05:06.008
     */
    public static String formatDateAsYearMonthDayHourMinuteSecondMillisecond(Date date, String utcOffset) {
        throw new RuntimeException();
    }

    /**
     * Convert int to a string
     * 
     * @param value
     * @return
     */
    public static String convertToString(Integer value) {
        throw new RuntimeException();
    }

    /**
     * Convert long to a string
     * 
     * @param value
     * @return
     */
    public static String convertToString(Long value) {
        throw new RuntimeException();
    }

    /**
     * Convert BigDecimal to a string
     * 
     * @param value
     * @return
     */
    public static String convertToString(BigDecimal value) {
        throw new RuntimeException();
    }

    /**
     * Convert string to a BigDecimal. 4 decimal places are reserved, the remainder
     * is
     * rounded up. Positive and negative numbers are supported.
     * 
     * @param value
     * @return
     */
    public static BigDecimal convertToBigDecimal(String value) {
        throw new RuntimeException();
    }

    /**
     * Convert Long to a BigDecimal. 4 decimal places are reserved, the remainder is
     * rounded up. Positive and negative numbers are supported.
     * 
     * @param value
     * @return
     */
    public static BigDecimal convertToBigDecimal(Long value) {
        throw new RuntimeException();
    }

    /**
     * Convert Integer to a BigDecimal. 4 decimal places are reserved, the remainder
     * is
     * rounded up. Positive and negative numbers are supported.
     * 
     * @param value
     * @return
     */
    public static BigDecimal convertToBigDecimal(Integer value) {
        throw new RuntimeException();
    }

    public static void registerCustomSqlFunction(JinqJPAStreamProvider jinqJPAStreamProvider) {
        for (var method : Arrays.asList(JPQLFunction.class.getMethods()).stream()
                .filter(s -> s.getName().equals("ifnull")).toList()) {
            jinqJPAStreamProvider.registerCustomSqlFunction(method, "IFNULL");
        }
        for (var method : Arrays.asList(JPQLFunction.class.getMethods()).stream()
                .filter(s -> s.getName().equals("foundTotalRowsForGroupBy")).toList()) {
            jinqJPAStreamProvider.registerCustomSqlFunction(method, "FOUND_ROWS");
        }
        for (var method : Arrays.asList(JPQLFunction.class.getMethods()).stream()
                .filter(s -> s.getName().equals("formatDateAsYearMonthDayHourMinuteSecondMillisecond"))
                .toList()) {
            jinqJPAStreamProvider.registerCustomSqlFunction(method,
                    "FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILLISECOND");
        }
        for (var method : Arrays.asList(JPQLFunction.class.getMethods()).stream()
                .filter(s -> s.getName().equals("formatDateAsYearMonthDayHourMinuteSecond"))
                .toList()) {
            jinqJPAStreamProvider.registerCustomSqlFunction(method,
                    "FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND");
        }
        for (var method : Arrays.asList(JPQLFunction.class.getMethods()).stream()
                .filter(s -> s.getName().equals("formatDateAsYearMonthDayHourMinute"))
                .toList()) {
            jinqJPAStreamProvider.registerCustomSqlFunction(method,
                    "FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE");
        }
        for (var method : Arrays.asList(JPQLFunction.class.getMethods()).stream()
                .filter(s -> s.getName().equals("formatDateAsYearMonthDayHour"))
                .toList()) {
            jinqJPAStreamProvider.registerCustomSqlFunction(method,
                    "FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR");
        }
        for (var method : Arrays.asList(JPQLFunction.class.getMethods()).stream()
                .filter(s -> s.getName().equals("formatDateAsYearMonthDay"))
                .toList()) {
            jinqJPAStreamProvider.registerCustomSqlFunction(method,
                    "FORMAT_DATE_AS_YEAR_MONTH_DAY");
        }
        for (var method : Arrays.asList(JPQLFunction.class.getMethods()).stream()
                .filter(s -> s.getName().equals("formatDateAsYearMonth"))
                .toList()) {
            jinqJPAStreamProvider.registerCustomSqlFunction(method,
                    "FORMAT_DATE_AS_YEAR_MONTH");
        }
        for (var method : Arrays.asList(JPQLFunction.class.getMethods()).stream()
                .filter(s -> s.getName().equals("formatDateAsYear"))
                .toList()) {
            jinqJPAStreamProvider.registerCustomSqlFunction(method,
                    "FORMAT_DATE_AS_YEAR");
        }
        for (var method : Arrays.asList(JPQLFunction.class.getMethods()).stream()
                .filter(s -> s.getName().equals("convertToBigDecimal"))
                .toList()) {
            jinqJPAStreamProvider.registerCustomSqlFunction(method,
                    "CONVERT_TO_BIG_DECIMAL");
        }
        for (var method : Arrays.asList(JPQLFunction.class.getMethods()).stream()
                .filter(s -> s.getName().equals("convertToString"))
                .toList()) {
            jinqJPAStreamProvider.registerCustomSqlFunction(method,
                    "CONVERT_TO_STRING");
        }
    }

}
