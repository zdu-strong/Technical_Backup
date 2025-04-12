package com.john.project.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class DateFormatProperties {

    @Value("${spring.jackson.date-format}")
    private String UTC;

    private String year = "yyyy";

    private String yearMonth = "yyyy-MM";

    private String yearMonthDay = "yyyy-MM-dd";

    private String yearMonthDayHour = "yyyy-MM-dd HH";

    private String yearMonthDayHourMinute = "yyyy-MM-dd HH:mm";

    private String yearMonthDayHourMinuteSecond = "yyyy-MM-dd HH:mm:ss";

    private String yearMonthDayHourMinuteSecondMillisecond = "yyyy-MM-dd HH:mm:ss.SSS";

}
