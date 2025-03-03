package com.springboot.project.test.scheduled.StorageSpaceScheduled;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.springboot.project.enums.DistributedExecutionEnum;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class StorageSpaceScheduledCleanDatabaseStorageTest extends BaseTest {

    @Test
    public void test() {
        this.distributedExecutionUtil.refreshData(DistributedExecutionEnum.STORAGE_SPACE_CLEAN);
    }

    @BeforeEach
    public void beforeEach() {
        Mockito.doCallRealMethod().when(this.distributedExecutionUtil)
                .refreshData(Mockito.any());
    }

}
