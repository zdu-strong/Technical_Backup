package com.springboot.project.test.scheduled.SystemInitScheduled;

import static org.junit.jupiter.api.Assertions.*;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.enumerate.SystemRoleEnum;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class SystemInitScheduledInitUserRoleAfterMoveOrganinzeTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        var userRoleList = this.userRoleService.getOrganizeRoleListByCompanyId(this.organizeId);
        assertEquals(2, userRoleList.size());
        assertTrue(JinqStream.from(userRoleList).map(s -> SystemRoleEnum.valueOfRole(s.getName())).toList()
                .contains(SystemRoleEnum.ORGANIZE_NORMAL_USER));
        assertTrue(JinqStream.from(userRoleList).map(s -> SystemRoleEnum.valueOfRole(s.getName())).toList()
                .contains(SystemRoleEnum.ORGANIZE_ADMIN));
        assertEquals(this.organizeId,
                JinqStream.from(userRoleList).select(s -> s.getOrganize().getId()).distinct().getOnlyValue());
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
