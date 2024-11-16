package com.springboot.project.test.service.DistributedExecutionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import com.springboot.project.enumerate.DistributedExecutionEnum;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class DistributedExecutionServiceGetLastDistributedExecutionTest extends BaseTest {

    @Test
    public void test() {
        var result = this.distributedExecutionService
                .getLastDistributedExecution(DistributedExecutionEnum.STORAGE_SPACE_CLEAN_DATABASE_STORAGE);
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertEquals(DistributedExecutionEnum.STORAGE_SPACE_CLEAN_DATABASE_STORAGE,
                DistributedExecutionEnum.valueOf(result.getExecutionType()));
        assertTrue(result.getIsDone());
        assertFalse(result.getHasError());
        assertEquals(1, result.getTotalRecord());
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
    }

    @BeforeEach
    public void beforeEach() {
        this.storage.storageResource(new ClassPathResource("email/email.xml"));
        this.storageSpaceScheduled.scheduled();
    }

}
