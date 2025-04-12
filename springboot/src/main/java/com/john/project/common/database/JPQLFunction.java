package com.john.project.common.database;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jinq.jpa.JinqJPAStreamProvider;
import com.john.project.properties.DateFormatProperties;
import cn.hutool.extra.spring.SpringUtil;

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
        return Optional.ofNullable(value).map(s -> new BigDecimal(s)).orElse(new BigDecimal(defaultValue));
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
        return Optional.ofNullable(value).map(s -> new BigDecimal(s)).orElse(new BigDecimal(defaultValue));
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
        return Optional.ofNullable(value).orElse(new BigDecimal(defaultValue));
    }

    /**
     * In order to obtain the total number of entries during paging, compatibility
     * processing has been done at the place of call. Please do not call it.
     * 
     * @return
     */
    public static Long foundTotalRowsForGroupBy() {
        throw new NotImplementedException();
    }

    /**
     * format date to string
     * 
     * @param utcOffset like +08:00
     * @return like 2022
     */
    public static String formatDateAsYear(Date date, String utcOffset) {
        return FastDateFormat.getInstance(SpringUtil.getBean(DateFormatProperties.class).getYear(),
                TimeZone.getTimeZone(utcOffset)).format(date);
    }

    /**
     * format date to string
     * 
     * @param utcOffset like +08:00
     * @return like 2022-08
     */
    public static String formatDateAsYearMonth(Date date, String utcOffset) {
        return FastDateFormat.getInstance(SpringUtil.getBean(DateFormatProperties.class).getYearMonth(),
                TimeZone.getTimeZone(utcOffset)).format(date);
    }

    /**
     * format date to string
     * 
     * @param utcOffset like +08:00
     * @return like 2022-08-08
     */
    public static String formatDateAsYearMonthDay(Date date, String utcOffset) {
        return FastDateFormat.getInstance(SpringUtil.getBean(DateFormatProperties.class).getYearMonthDay(),
                TimeZone.getTimeZone(utcOffset)).format(date);
    }

    /**
     * format date to string
     * 
     * @param utcOffset like +08:00
     * @return like 2022-08-08 13
     */
    public static String formatDateAsYearMonthDayHour(Date date, String utcOffset) {
        return FastDateFormat.getInstance(SpringUtil.getBean(DateFormatProperties.class).getYearMonthDayHour(),
                TimeZone.getTimeZone(utcOffset)).format(date);
    }

    /**
     * format date to string
     * 
     * @param utcOffset like +08:00
     * @return like 2022-08-08 13:05
     */
    public static String formatDateAsYearMonthDayHourMinute(Date date, String utcOffset) {
        return FastDateFormat.getInstance(SpringUtil.getBean(DateFormatProperties.class).getYearMonthDayHourMinute(),
                TimeZone.getTimeZone(utcOffset)).format(date);
    }

    /**
     * format date to string
     * 
     * @param utcOffset like +08:00
     * @return like 2022-08-08 13:05:06
     */
    public static String formatDateAsYearMonthDayHourMinuteSecond(Date date, String utcOffset) {
        return FastDateFormat
                .getInstance(SpringUtil.getBean(DateFormatProperties.class).getYearMonthDayHourMinuteSecond(),
                        TimeZone.getTimeZone(utcOffset))
                .format(date);
    }

    /**
     * format date to string
     * 
     * @param utcOffset like +08:00
     * @return like 2022-08-08 13:05:06.008
     */
    public static String formatDateAsYearMonthDayHourMinuteSecondMillisecond(Date date, String utcOffset) {
        return FastDateFormat.getInstance(
                SpringUtil.getBean(DateFormatProperties.class).getYearMonthDayHourMinuteSecondMillisecond(),
                TimeZone.getTimeZone(utcOffset)).format(date);
    }

    /**
     * Convert int to a string
     * 
     * @param value
     * @return
     */
    public static String convertToString(Integer value) {
        return String.valueOf(value);
    }

    /**
     * Convert long to a string
     * 
     * @param value
     * @return
     */
    public static String convertToString(Long value) {
        return String.valueOf(value);
    }

    /**
     * Convert BigDecimal to a string
     * 
     * @param value
     * @return
     */
    public static String convertToString(BigDecimal value) {
        return Optional.ofNullable(value).map(s -> s.toPlainString()).orElse(null);
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
        return new BigDecimal(value);
    }

    /**
     * Convert Long to a BigDecimal. 4 decimal places are reserved, the remainder is
     * rounded up. Positive and negative numbers are supported.
     * 
     * @param value
     * @return
     */
    public static BigDecimal convertToBigDecimal(Long value) {
        return new BigDecimal(value);
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
        return new BigDecimal(value);
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
