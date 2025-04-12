package com.john.project.test.scheduled.OrganizeRelationRefreshScheduled;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.john.project.enums.DistributedExecutionEnum;
import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class OrganizeRelationRefreshScheduledTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        this.distributedExecutionUtil.refreshData(DistributedExecutionEnum.ORGANIZE_CLOSURE_REFRESH);
        var result = this.organizeService.searchByName(1L, 20L, "Son Gohan", this.organizeId);
        assertEquals(1, result.getTotalRecords());
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
            assertEquals(1, result.getTotalRecords());
        }
        {
            var parentOrganizeModel = new OrganizeModel().setName("Piccolo");
            var parentOrganize = this.organizeService.create(parentOrganizeModel);
            var result = this.organizeService.searchByName(1L, 20L, "Son Gohan", parentOrganize.getId());
            assertEquals(0, result.getTotalRecords());
            this.organizeService.move(organizeId, parentOrganize.getId());
            result = this.organizeService.searchByName(1L, 20L, "Son Gohan", parentOrganize.getId());
            assertEquals(0, result.getTotalRecords());
            this.organizeId = parentOrganize.getId();
        }
        {
            Mockito.doCallRealMethod().when(this.distributedExecutionUtil)
                    .refreshData(Mockito.any());
        }
    }


}
