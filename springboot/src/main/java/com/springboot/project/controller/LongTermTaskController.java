package com.springboot.project.controller;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.springboot.project.common.baseController.BaseController;
import com.springboot.project.enumerate.LongTermTaskTempWaitDurationEnum;
import com.springboot.project.model.LongTermTaskModel;

@RestController
public class LongTermTaskController extends BaseController {

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

        this.longTermTaskCheckService
                .checkIsExistLongTermTaskById(this.encryptDecryptService.decryptByAES(id));

        var expireDate = DateUtils.addMilliseconds(new Date(),
                Long.valueOf(LongTermTaskTempWaitDurationEnum.TEMP_WAIT_DURATION.toMillis()).intValue());

        while (true) {
            var response = this.longTermTaskService
                    .getLongTermTask(this.encryptDecryptService.decryptByAES(id));
            if (response.getBody() instanceof LongTermTaskModel) {
                var longTermTaskResult = this.objectMapper.readValue(
                        this.objectMapper.writeValueAsString(response.getBody()),
                        new TypeReference<LongTermTaskModel<Object>>() {
                        });
                if (longTermTaskResult.getIsDone() || !new Date().before(expireDate)) {
                    return response;
                } else {
                    Thread.sleep(1);
                }
            } else {
                return response;
            }
        }
    }
}
