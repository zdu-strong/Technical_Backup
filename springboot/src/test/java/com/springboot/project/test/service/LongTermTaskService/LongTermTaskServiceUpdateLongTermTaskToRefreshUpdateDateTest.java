package com.springboot.project.test.service.LongTermTaskService;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Date;

import org.apache.commons.lang3.ThreadUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.springboot.project.model.LongTermTaskModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class LongTermTaskServiceUpdateLongTermTaskToRefreshUpdateDateTest extends BaseTest {

    private String longTermtaskId;
    private Date updateDate;

    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        this.longTermTaskService.updateLongTermTaskToRefreshUpdateDate(this.longTermtaskId);
        var result = (ResponseEntity<LongTermTaskModel<?>>) this.longTermTaskService
                .getLongTermTask(this.longTermtaskId);
        assertTrue(result.getBody().getUpdateDate().after(this.updateDate));
    }

    @BeforeEach
    public void BeforeEach() {
        this.longTermtaskId = this.longTermTaskService.createLongTermTask();
        this.updateDate = ((LongTermTaskModel<?>) this.longTermTaskService.getLongTermTask(this.longTermtaskId)
                .getBody()).getUpdateDate();
        ThreadUtils.sleepQuietly(Duration.ofMillis(1));
    }

}
