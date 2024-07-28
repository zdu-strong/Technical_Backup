package com.springboot.project.controller;

import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.springboot.project.common.baseController.BaseController;
import com.springboot.project.enumerate.LongTermTaskTempWaitDurationEnum;
import com.springboot.project.model.LongTermTaskModel;

@RestController
public class LongTermTaskController extends BaseController {

    @GetMapping("/long_term_task/is_done")
    public ResponseEntity<?> getLongTermTaskIsDone(@RequestParam String id)
            throws InterruptedException, JsonMappingException, JsonProcessingException {
        id = this.encryptDecryptService.decryptByAES(id);

        this.longTermTaskCheckService
                .checkIsExistLongTermTaskById(id);

        var expireDate = DateUtils.addMilliseconds(new Date(),
                Long.valueOf(LongTermTaskTempWaitDurationEnum.TEMP_WAIT_DURATION.toMillis()).intValue());

        while (true) {
            var response = this.longTermTaskService
                    .getLongTermTask(id);
            if (response.getBody() instanceof LongTermTaskModel) {
                var longTermTaskResult = this.objectMapper.readValue(
                        this.objectMapper.writeValueAsString(response.getBody()),
                        new TypeReference<LongTermTaskModel<Object>>() {
                        });
                if (longTermTaskResult.getIsDone() || !new Date().before(expireDate)) {
                    return ResponseEntity.ok(longTermTaskResult.getIsDone());
                } else {
                    Thread.sleep(1);
                }
            } else {
                return response;
            }
        }
    }

    /**
     * Because some requests take a long time to execute, so provide this
     * asynchronous task api. Call them first, like this: (
     * this.longTermTaskUtil.run(()->{});). And then call this api for polling to
     * obtain the execution results.
     * 
     * @param id
     * @return
     * @throws InterruptedException
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    @GetMapping("/long_term_task")
    public ResponseEntity<?> getLongTermTask(@RequestParam String id)
            throws InterruptedException, JsonMappingException, JsonProcessingException {

        id = this.encryptDecryptService.decryptByAES(id);

        this.longTermTaskCheckService
                .checkIsExistLongTermTaskById(id);

        var response = this.longTermTaskService
                .getLongTermTask(id);
        if (response.getBody() instanceof LongTermTaskModel) {
            var longTermTaskResult = this.objectMapper.readValue(
                    this.objectMapper.writeValueAsString(response.getBody()),
                    new TypeReference<LongTermTaskModel<Object>>() {
                    });
            if (!longTermTaskResult.getIsDone()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The task is not completed");
            }
            return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders())
                    .body(longTermTaskResult.getResult());
        } else {
            return response;
        }
    }
}
