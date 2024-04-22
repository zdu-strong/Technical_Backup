package com.springboot.project.test.scheduled.StorageSpaceScheduled;

import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class StorageSpaceScheduledCleanDatabaseStorageTest extends BaseTest {

    @Test
    public void test() throws InterruptedException, ExecutionException {
        this.storageSpaceScheduled.scheduled();
    }

    @BeforeEach
    public void beforeEach() {
        Mockito.doCallRealMethod().when(this.storageSpaceScheduled).cleanDatabaseStorage();
    }

}
