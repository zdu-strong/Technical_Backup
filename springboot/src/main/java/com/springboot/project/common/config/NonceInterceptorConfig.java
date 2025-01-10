package com.springboot.project.common.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jinq.tuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.project.constant.NonceConstant;
import com.springboot.project.format.LongTermTaskFormatter;
import com.springboot.project.properties.DateFormatProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

@Component
public class NonceInterceptorConfig implements HandlerInterceptor {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LongTermTaskFormatter longTermTaskFormatter;

    @Autowired
    private DateFormatProperties dateFormatProperties;

    private ConcurrentHashMap<String, String> nonceMap = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        var nonce = request.getHeader("X-Nonce");
        var timestampString = request.getHeader("X-Timestamp");
        if (StringUtils.isBlank(nonce)) {
            return true;
        }
        if (StringUtils.isBlank(timestampString)) {
            return true;
        }
        var timestamp = convertDateStringToDate(timestampString);
        if (timestamp == null) {
            return writeErrorMessageToReponse("Invalid timestamp", response);
        }
        if (timestamp
                .after(DateUtils.addMilliseconds(new Date(), (int) NonceConstant.NONCE_SURVIVAL_DURATION.toMillis()))) {
            return writeErrorMessageToReponse("Nonce has expired", response);
        }
        if (timestamp.before(
                DateUtils.addMilliseconds(new Date(), (int) -NonceConstant.NONCE_SURVIVAL_DURATION.toMillis()))) {
            return writeErrorMessageToReponse("Nonce has expired", response);
        }
        nonce = Base64.getEncoder().encodeToString(
                this.objectMapper.writeValueAsString(new Pair<>(timestamp, nonce)).getBytes(StandardCharsets.UTF_8));
        if (nonce.length() > 255) {
            return writeErrorMessageToReponse("Invalid nonce", response);
        }

        if (StringUtils.isBlank(nonceMap.putIfAbsent(nonce, nonce))) {
            return true;
        }

        return writeErrorMessageToReponse("Duplicate nonce detected", response);
    }

    private Date convertDateStringToDate(String timestampString) {
        try {
            return FastDateFormat.getInstance(this.dateFormatProperties.getUTC()).parse(timestampString);
        } catch (Throwable e) {
            return null;
        }
    }

    @SneakyThrows
    private boolean writeErrorMessageToReponse(String message, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter()
                .write(this.objectMapper.writeValueAsString(this.objectMapper
                        .readTree(new String(Base64.getDecoder().decode(this.longTermTaskFormatter
                                .formatThrowable(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        message))),
                                StandardCharsets.UTF_8))
                        .get("body")));
        response.getWriter().flush();
        response.getWriter().close();
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }
}