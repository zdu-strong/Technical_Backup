package com.springboot.project.common.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jinq.tuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.project.constant.NonceConstant;
import com.springboot.project.properties.DateFormatProperties;
import com.springboot.project.service.NonceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;

@Configuration
@ControllerAdvice
public class NonceControllerAdviceConfig {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DateFormatProperties dateFormatProperties;

    @Autowired
    private NonceService nonceService;

    @Autowired
    private HttpServletRequest request;

    @ModelAttribute
    @SneakyThrows
    public void checkNonce(@RequestHeader(name = "X-Nonce", required = false) String nonce,
            @RequestHeader(name = "X-Timestamp", required = false) String timestampString) {
        if (StringUtils.equalsIgnoreCase(request.getRequestURI(), "/error")) {
            return;
        }
        if (StringUtils.isBlank(nonce)) {
            return;
        }
        if (StringUtils.isBlank(timestampString)) {
            return;
        }
        var timestamp = convertDateStringToDate(timestampString);
        if (timestamp
                .after(DateUtils.addMilliseconds(new Date(), (int) NonceConstant.NONCE_SURVIVAL_DURATION.toMillis()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nonce has expired");
        }
        if (timestamp.before(
                DateUtils.addMilliseconds(new Date(), (int) -NonceConstant.NONCE_SURVIVAL_DURATION.toMillis()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nonce has expired");
        }
        nonce = Base64.getEncoder().encodeToString(
                this.objectMapper.writeValueAsString(new Pair<>(timestamp, nonce)).getBytes(StandardCharsets.UTF_8));
        if (nonce.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid nonce");
        }

        try {
            this.nonceService.create(nonce);
        } catch (DataIntegrityViolationException | JpaSystemException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate nonce detected");
        }
    }

    private Date convertDateStringToDate(String timestampString) {
        try {
            return FastDateFormat.getInstance(this.dateFormatProperties.getUTC()).parse(timestampString);
        } catch (Throwable e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid timestamp");
        }
    }

}