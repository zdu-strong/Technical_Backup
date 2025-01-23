package com.springboot.project.test.scheduled.StorageSpaceScheduled;

import org.junit.jupiter.api.Test;

import com.springboot.project.enumerate.DistributedExecutionEnum;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class StorageSpaceScheduledCleanDatabaseStorageTest extends BaseTest {

    @Test
    public void test() {
        this.distributedExecutionUtil.refreshData(DistributedExecutionEnum.STORAGE_SPACE_CLEAN_DATABASE_STORAGE);
    }

}
