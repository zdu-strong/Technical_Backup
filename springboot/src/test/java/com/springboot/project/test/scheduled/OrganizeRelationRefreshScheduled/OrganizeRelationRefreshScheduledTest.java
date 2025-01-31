package com.springboot.project.test.scheduled.OrganizeRelationRefreshScheduled;

import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.springboot.project.enums.DistributedExecutionEnum;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.common.BaseTest.BaseTest;
import io.reactivex.rxjava3.core.Flowable;

public class OrganizeRelationRefreshScheduledTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        this.distributedExecutionUtil.refreshData(DistributedExecutionEnum.ORGANIZE_CLOSURE_REFRESH);
        var result = this.organizeService.searchByName(1L, 20L, "Son Gohan", this.organizeId);
        assertEquals(1, result.getTotalRecord());
    }

    @BeforeEach
    public void beforeEach() {
        {
            var parentOrganizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
            var parentOrganize = this.organizeService.create(parentOrganizeModel);
            var childOrganizeModel = new OrganizeModel().setName("Son Gohan").setParent(parentOrganize);
            var childOrganize = this.organizeService.create(childOrganizeModel);
            this.organizeId = childOrganize.getId();
            this.organizeUtil.refresh(parentOrganize.getId());
            var result = this.organizeService.searchByName(1L, 20L, "Son Gohan", parentOrganize.getId());
            assertEquals(1, result.getTotalRecord());
        }
        {
            var parentOrganizeModel = new OrganizeModel().setName("Piccolo");
            var parentOrganize = this.organizeService.create(parentOrganizeModel);
            var result = this.organizeService.searchByName(1L, 20L, "Son Gohan", parentOrganize.getId());
            assertEquals(0, result.getTotalRecord());
            this.organizeService.move(organizeId, parentOrganize.getId());
            result = this.organizeService.searchByName(1L, 20L, "Son Gohan", parentOrganize.getId());
            assertEquals(0, result.getTotalRecord());
            this.organizeId = parentOrganize.getId();
        }
        {
            Flowable.interval(1, TimeUnit.MILLISECONDS)
                    .concatMap(s -> {
                        this.distributedExecutionUtil.refreshData(DistributedExecutionEnum.ORGANIZE_CLOSURE_REFRESH);
                        var result = this.organizeService.searchByName(1L, 20L, "Son Gohan", this.organizeId);
                        if (result.getTotalRecord() == 1) {
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
