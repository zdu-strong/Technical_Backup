package com.springboot.project.test.service.DistributedExecutionMainService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;

import com.springboot.project.enums.DistributedExecutionEnum;
import com.springboot.project.test.common.BaseTest.BaseTest;

import io.reactivex.rxjava3.core.Flowable;

public class DistributedExecutionMainServiceGetLastDoneDistributedExecutionTest extends BaseTest {

    @Test
    public void test() {
        var result = this.distributedExecutionMainService
                .getLastDoneDistributedExecution(DistributedExecutionEnum.STORAGE_SPACE_CLEAN);
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertEquals(DistributedExecutionEnum.STORAGE_SPACE_CLEAN,
                DistributedExecutionEnum.parse(result.getExecutionType()));
        assertTrue(result.getIsDone());
        assertFalse(result.getHasError());
        assertEquals(1, result.getTotalRecord());
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
    }

    @BeforeEach
    public void beforeEach() {
        this.storage.storageResource(new ClassPathResource("email/email.xml"));
        Mockito.doCallRealMethod().when(this.distributedExecutionUtil).refreshData(Mockito.any());
        {
            Flowable.interval(1, TimeUnit.MILLISECONDS)
                    .concatMap(s -> {
                        this.distributedExecutionUtil.refreshData(DistributedExecutionEnum.STORAGE_SPACE_CLEAN);
                        var result = this.distributedExecutionMainService
                                .getLastDoneDistributedExecution(DistributedExecutionEnum.STORAGE_SPACE_CLEAN);
                        if (result != null) {
                            return Flowable.just(StringUtils.EMPTY);
                        } else {
                            return Flowable.empty();
                        }
                    })
                    .take(1)
                    .timeout(2, TimeUnit.MINUTES)
                    .blockingSubscribe();
        }
    }

}
