package com.springboot.project.test.scheduled.SystemInitScheduled;

import static org.junit.jupiter.api.Assertions.*;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.enumerate.SystemRoleEnum;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class SystemInitScheduledInitUserRoleAfterCreateOrganinzeTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        var organizeRoleList = this.userRoleService.getOrganizeRoleListByCompanyId(this.organizeId);
        assertEquals(2, organizeRoleList.size());
        assertTrue(JinqStream.from(organizeRoleList).map(s -> SystemRoleEnum.valueOf(s.getName()))
                .toList()
                .contains(SystemRoleEnum.ORGANIZE_NORMAL_USER));
        assertTrue(JinqStream.from(organizeRoleList).map(s -> SystemRoleEnum.valueOf(s.getName()))
                .toList()
                .contains(SystemRoleEnum.ORGANIZE_ADMIN));
        assertEquals(this.organizeId,
                JinqStream.from(organizeRoleList).select(s -> s.getOrganize().getId()).distinct().getOnlyValue());
    }

    @BeforeEach
    public void beforeEach() {
        this.systemInitScheduled.scheduled();
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        this.organizeId = this.organizeService.create(organizeModel).getId();
    }

}
