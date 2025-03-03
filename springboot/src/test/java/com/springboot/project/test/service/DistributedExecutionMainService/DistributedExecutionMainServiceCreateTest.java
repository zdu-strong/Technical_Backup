package com.springboot.project.test.service.DistributedExecutionMainService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import com.springboot.project.enums.DistributedExecutionEnum;
import com.springboot.project.enums.DistributedExecutionMainStatusEnum;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class DistributedExecutionMainServiceCreateTest extends BaseTest {

    @Test
    public void test() {
        var result = this.distributedExecutionMainService
                .create(DistributedExecutionEnum.STORAGE_SPACE_CLEAN);
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertEquals(DistributedExecutionEnum.STORAGE_SPACE_CLEAN,
                DistributedExecutionEnum.parse(result.getExecutionType()));
        assertEquals(DistributedExecutionMainStatusEnum.IN_PROGRESS.getValue(), result.getStatus());
        assertEquals(1, result.getTotalPage());
        assertEquals(1, result.getTotalPartition());
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
    }

    @BeforeEach
    public void beforeEach() {
        this.storage.storageResource(new ClassPathResource("email/email.xml"));
    }

}
