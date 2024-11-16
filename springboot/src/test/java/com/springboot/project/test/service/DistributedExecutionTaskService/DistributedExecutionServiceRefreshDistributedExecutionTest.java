package com.springboot.project.test.service.DistributedExecutionTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import com.springboot.project.enumerate.DistributedExecutionEnum;
import com.springboot.project.model.DistributedExecutionModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class DistributedExecutionServiceRefreshDistributedExecutionTest extends BaseTest {

    private DistributedExecutionModel distributedExecutionModel;

    @Test
    public void test() {
        var result = this.distributedExecutionTaskService
                .create(distributedExecutionModel.getId(), 1);
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertFalse(result.getIsDone());
        assertFalse(result.getHasError());
        assertEquals(1, result.getPageNum());
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
        assertEquals(this.distributedExecutionModel.getId(), result.getDistributedExecutionModel().getId());
    }

    @BeforeEach
    public void beforeEach() {
        this.storage.storageResource(new ClassPathResource("email/email.xml"));
        this.distributedExecutionModel = this.distributedExecutionService
                .create(DistributedExecutionEnum.STORAGE_SPACE_CLEAN_DATABASE_STORAGE, 1);
    }

}
