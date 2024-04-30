package com.springboot.project.format;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Lists;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.LongTermTaskEntity;
import com.springboot.project.model.LongTermTaskModel;

import ch.qos.logback.classic.spi.ThrowableProxy;

@Service
public class LongTermTaskFormatter extends BaseService {

    private Duration tempTaskSurvivalDuration = Duration.ofMinutes(1);

    public String formatThrowable(Throwable e) {
        try {
            var map = new HashMap<String, Object>();
            if (e instanceof ResponseStatusException) {
                map.put("message", ((ResponseStatusException) e).getReason());
                map.put("status", ((ResponseStatusException) e).getStatusCode().value());
            } else {
                map.put("message", e.getMessage());
                map.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            map.put("timestamp",
                    this.objectMapper.readValue(this.objectMapper.writeValueAsString(new Date()), String.class));
            map.put("error", HttpStatus.valueOf(Integer.valueOf(String.valueOf(map.get("status")))).getReasonPhrase());
            var traceList = Lists.newArrayList();
            var stackTraceElement = JinqStream.from(Lists.newArrayList(e.getStackTrace()))
                    .findFirst().get();
            traceList.add(stackTraceElement.getClassName() + ": " + map.get("message"));
            traceList.addAll(JinqStream.from(Lists.newArrayList(new ThrowableProxy(e).getStackTraceElementProxyArray()))
                    .select(s -> "\t" + s.getSTEAsString()).toList());
            map.put("trace", traceList);
            var text = this.objectMapper.writeValueAsString(map);
            return text;
        } catch (JsonProcessingException e1) {
            throw new RuntimeException(e1.getMessage(), e1);
        }
    }

    public ResponseEntity<?> format(LongTermTaskEntity longTermTaskEntity) {
        try {
            var expireDate = DateUtils.addMilliseconds(new Date(),
                    Long.valueOf(0 - this.tempTaskSurvivalDuration.toMillis()).intValue());
            if (!longTermTaskEntity.getIsDone() && longTermTaskEntity.getUpdateDate().before(expireDate)) {
                throw new RuntimeException("The task failed because it stopped");
            }

            var longTermTaskModel = new LongTermTaskModel<Object>();
            BeanUtils.copyProperties(longTermTaskEntity, longTermTaskModel);

            if (longTermTaskEntity.getIsDone()) {
                var result = this.objectMapper.readTree(longTermTaskEntity.getResult());
                longTermTaskModel.setResult(this.objectMapper
                        .readValue(this.objectMapper.writeValueAsString(result.get("body")), Object.class));
                HttpHeaders httpHeaders = new HttpHeaders();
                result.get("headers").fields().forEachRemaining((s) -> {
                    try {
                        httpHeaders.addAll(s.getKey(),
                                this.objectMapper.readValue(this.objectMapper.writeValueAsString(s.getValue()),
                                        new TypeReference<List<String>>() {
                                        }));
                    } catch (JsonMappingException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                });
                if (String.valueOf(result.get("statusCodeValue").asInt()).startsWith("2")) {
                    ResponseEntity<LongTermTaskModel<?>> response = ResponseEntity
                            .status(result.get("statusCodeValue").asInt())
                            .headers(httpHeaders).body(longTermTaskModel);
                    return response;
                } else {
                    ResponseEntity<?> response = ResponseEntity
                            .status(result.get("statusCodeValue").asInt())
                            .headers(httpHeaders).body(result.get("body"));
                    return response;
                }
            } else {
                return ResponseEntity.ok().body(longTermTaskModel);
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
