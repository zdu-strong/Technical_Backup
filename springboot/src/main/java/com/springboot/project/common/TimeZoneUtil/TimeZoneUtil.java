package com.springboot.project.common.TimeZoneUtil;

import java.time.Instant;
import java.util.Arrays;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class TimeZoneUtil {

    /**
     * return value like +08:00
     * 
     * @param timeZone
     * @return
     */
    public String getUtcOffset(String timeZone) {
        checkTimeZoneString(timeZone);
        return getUtcOffset(TimeZone.getTimeZone(timeZone));
    }

    /**
     * return value like +08:00
     * 
     * @param timeZone
     * @return
     */
    public String getUtcOffset(TimeZone timeZone) {
        var zoneId = timeZone.toZoneId();
        var zonedDateTime = Instant.now().atZone(zoneId);
        var utcOffset = String.format("%tz", zonedDateTime);
        utcOffset = utcOffset.substring(0, 3) + ":" + utcOffset.substring(3, 5);
        checkUtcOffset(utcOffset);
        return utcOffset;
    }

    public void checkTimeZone(String timeZone) {
        this.getUtcOffset(timeZone);
    }

    private void checkTimeZoneString(String timeZone) {
        if (!Arrays.asList(TimeZone.getAvailableIDs()).contains(timeZone)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time zone");
        }
    }

    private void checkUtcOffset(String utcOffset) {
        if (utcOffset.length() != 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time zone");
        }
        if (!Pattern.compile(
                "^[" + Pattern.quote("+") + Pattern.quote("-") + "]{1}" + "[0-9]{2}" + Pattern.quote(":") + "[0-9]{2}$")
                .asPredicate().test(utcOffset)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time zone");
        }
    }

}
