package com.springboot.project.test.service.DistributedExecutionDetailService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import com.springboot.project.enums.DistributedExecutionEnum;
import com.springboot.project.model.DistributedExecutionMainModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class DistributedExecutionDetailServiceRefreshDistributedExecutionTest extends BaseTest {

    private DistributedExecutionMainModel distributedExecutionMainModel;

    @Test
    public void test() {
        var result = this.distributedExecutionDetailService.createByResult(distributedExecutionMainModel.getId(), 1, 1);
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertFalse(result.getIsDone());
        assertFalse(result.getHasError());
        assertEquals(1, result.getPageNum());
        assertEquals(1, result.getPartitionNum());
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
        assertEquals(this.distributedExecutionMainModel.getId(), result.getDistributedExecutionMain().getId());
    }

    @BeforeEach
    public void beforeEach() {
        this.storage.storageResource(new ClassPathResource("email/email.xml"));
        this.distributedExecutionMainModel = this.distributedExecutionMainService
                .create(DistributedExecutionEnum.STORAGE_SPACE_CLEAN);
    }

}
