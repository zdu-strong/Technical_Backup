package com.springboot.project.common.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.project.format.LongTermTaskFormatter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class NonceInterceptorConfig implements HandlerInterceptor {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LongTermTaskFormatter longTermTaskFormatter;

    private ConcurrentHashMap<String, String> nonceMap = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        var nonce = request.getHeader("X-Nonce");
        if (StringUtils.isBlank(nonce)) {
            return true;
        }
        if (StringUtils.isBlank(nonceMap.putIfAbsent(nonce, nonce))) {
            return true;
        }
        // Invalid nonce
        // Nonce already used
        // Duplicate nonce detected
        // Nonce has expired
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter()
                .write(this.objectMapper.writeValueAsString(this.objectMapper
                        .readTree(new String(Base64.getDecoder().decode(this.longTermTaskFormatter
                                .formatThrowable(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Duplicate nonce detected"))),
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