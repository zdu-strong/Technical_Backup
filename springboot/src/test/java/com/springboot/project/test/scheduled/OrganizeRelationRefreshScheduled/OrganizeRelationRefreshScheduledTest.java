package com.springboot.project.test.scheduled.OrganizeRelationRefreshScheduled;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class OrganizeRelationRefreshScheduledTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        this.organizeRelationRefreshScheduled.scheduled();
        var result = this.organizeService.searchByName(1L, 20L, "Son Gohan", this.organizeId);
        assertEquals(1, result.getTotalRecord());
    }

    @BeforeEach
    public void beforeEach() {
        Mockito.doCallRealMethod().when(this.organizeRelationRefreshScheduled).scheduled();
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
    }

}
