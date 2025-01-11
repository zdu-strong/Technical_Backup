package com.springboot.project.format;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.constant.LongTermTaskTempWaitDurationConstant;
import com.springboot.project.entity.LongTermTaskEntity;
import com.springboot.project.model.LongTermTaskModel;
import com.springboot.project.model.LongTermTaskUniqueKeyModel;
import lombok.SneakyThrows;

@Service
public class LongTermTaskFormatter extends BaseService {

    @SneakyThrows
    public String formatResult(Object result) {
        return Base64.getEncoder()
                .encodeToString(this.objectMapper.writeValueAsString(result).getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    public String formatLongTermTaskUniqueKey(LongTermTaskUniqueKeyModel longTermTaskUniqueKey) {
        var uniqueKeyJsonString = this.objectMapper.writeValueAsString(longTermTaskUniqueKey);
        return uniqueKeyJsonString;
    }

    @SneakyThrows
    public String formatThrowable(Throwable e) {
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
        var text = this.objectMapper.writeValueAsString(map);
        var body = this.objectMapper.readValue(text, Object.class);
        var responseEntity = e instanceof ResponseStatusException
                ? ResponseEntity.status(((ResponseStatusException) e).getStatusCode())
                : ResponseEntity.internalServerError();
        var response = responseEntity.body(body);
        return this.formatResult(response);
    }

    @SneakyThrows
    public ResponseEntity<?> format(LongTermTaskEntity longTermTaskEntity) {
        var expireDate = DateUtils.addMilliseconds(new Date(),
                Long.valueOf(0 - LongTermTaskTempWaitDurationConstant.TEMP_TASK_SURVIVAL_DURATION.toMillis())
                        .intValue());
        if (!longTermTaskEntity.getIsDone() && longTermTaskEntity.getUpdateDate().before(expireDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The task failed because it stopped");
        }

        var longTermTaskModel = new LongTermTaskModel<Object>();
        BeanUtils.copyProperties(longTermTaskEntity, longTermTaskModel);
        longTermTaskModel
                .setLongTermTaskUniqueKey(this.objectMapper.readValue(longTermTaskEntity.getUniqueKeyJsonString(),
                        LongTermTaskUniqueKeyModel.class));
        if (longTermTaskEntity.getIsDone()) {
            var result = this.objectMapper.readTree(
                    new String(Base64.getDecoder().decode(longTermTaskEntity.getResult()), StandardCharsets.UTF_8));
            longTermTaskModel.setResult(this.objectMapper
                    .readValue(this.objectMapper.writeValueAsString(result.get("body")), Object.class));
            HttpHeaders httpHeaders = new HttpHeaders();
            result.get("headers").fields().forEachRemaining(new Consumer<Entry<String, JsonNode>>() {

                @Override
                @SneakyThrows
                public void accept(Entry<String, JsonNode> s) {
                    httpHeaders.addAll(s.getKey(),
                            objectMapper.readValue(objectMapper.writeValueAsString(s.getValue()),
                                    new TypeReference<List<String>>() {
                                    }));
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

    }
}
