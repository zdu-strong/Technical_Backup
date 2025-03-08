package com.springboot.project.test.scheduled.SystemInitScheduled;

import static org.junit.jupiter.api.Assertions.*;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.enums.SystemRoleEnum;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class SystemInitScheduledInitUserRoleAfterMoveOrganinzeTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        var roleList = this.roleOrganizeRelationService
                .searchOrganizeRoleForSuperAdminByPagination(1, SystemRoleEnum.values().length, organizeId, false)
                .getList();
        assertEquals(2, roleList.size());
        assertTrue(JinqStream.from(roleList).map(s -> SystemRoleEnum.parse(s.getName())).toList()
                .contains(SystemRoleEnum.ORGANIZE_VIEW));
        assertTrue(JinqStream.from(roleList).map(s -> SystemRoleEnum.parse(s.getName())).toList()
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
        var parent = this.organizeService.create(organizeModel);
        this.organizeId = this.organizeService.create(new OrganizeModel().setName("Son Gohan").setParent(parent))
                .getId();
        this.organizeUtil.move(organizeId, null);
    }

}
