package com.springboot.project.test.service.DistributedExecutionMainService;

import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import com.springboot.project.enums.DistributedExecutionEnum;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class DistributedExecutionMainServiceGetDistributedExecutionWithInprogressTest extends BaseTest {

    @Test
    public void test() {
        var result = this.distributedExecutionMainService
                .getDistributedExecutionWithInprogress(DistributedExecutionEnum.STORAGE_SPACE_CLEAN);
        assertNull(result);
    }

    @BeforeEach
    public void beforeEach() {
        this.storage.storageResource(new ClassPathResource("email/email.xml"));
        this.distributedExecutionUtil.refreshData(DistributedExecutionEnum.STORAGE_SPACE_CLEAN);
    }

}
