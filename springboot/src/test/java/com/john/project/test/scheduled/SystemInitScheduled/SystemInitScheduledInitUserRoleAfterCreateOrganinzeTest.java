package com.john.project.test.scheduled.SystemInitScheduled;

import static org.junit.jupiter.api.Assertions.*;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.john.project.enums.DistributedExecutionEnum;
import com.john.project.enums.SystemRoleEnum;
import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class SystemInitScheduledInitUserRoleAfterCreateOrganinzeTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        var roleList = this.roleOrganizeRelationService
                .searchOrganizeRoleForSuperAdminByPagination(1, SystemRoleEnum.values().length, organizeId, false)
                .getItems();
        assertEquals(2, roleList.size());
        assertTrue(JinqStream.from(roleList).map(s -> SystemRoleEnum.parse(s.getName()))
                .toList()
                .contains(SystemRoleEnum.ORGANIZE_VIEW));
        assertTrue(JinqStream.from(roleList).map(s -> SystemRoleEnum.parse(s.getName()))
                .toList()
                .contains(SystemRoleEnum.ORGANIZE_MANAGE));
        assertEquals(this.organizeId,
                JinqStream.from(roleList)
                        .selectAllList(s -> s.getOrganizeList())
                        .select(s -> s.getId())
                        .distinct()
                        .getOnlyValue());
    }

    @BeforeEach
    public void beforeEach() {
        this.systemInitScheduled.scheduled();
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        this.organizeId = this.organizeService.create(organizeModel).getId();
        Mockito.doCallRealMethod().when(this.distributedExecutionUtil).refreshData(Mockito.any());
        this.distributedExecutionUtil.refreshData(DistributedExecutionEnum.ORGANIZE_CLOSURE_REFRESH);
    }

}
