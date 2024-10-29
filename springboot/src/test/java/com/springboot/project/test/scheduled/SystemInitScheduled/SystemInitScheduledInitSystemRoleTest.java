package com.springboot.project.test.scheduled.SystemInitScheduled;

import static org.junit.jupiter.api.Assertions.*;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.Test;
import com.springboot.project.enumerate.SystemRoleEnum;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class SystemInitScheduledInitSystemRoleTest extends BaseTest {

    @Test
    public void test() {
        this.systemInitScheduled.scheduled();
        var paginationModel = this.systemRoleService.searchUserRoleForSuperAdminByPagination(1, 200);
        var systemRoleList = paginationModel.getList();
        assertEquals(1, systemRoleList.size());
        var systemRole = JinqStream.from(systemRoleList).getOnlyValue();
        assertEquals(36, systemRole.getId().length());
        assertEquals(SystemRoleEnum.SUPER_ADMIN.getRole(), systemRole.getName());
        assertNotNull(systemRole.getCreateDate());
        assertNotNull(systemRole.getUpdateDate());
        assertNull(systemRole.getOrganize());
        assertEquals(1, systemRole.getSystemDefaultRoleList().size());
        var systemDefaultRole = JinqStream.from(systemRole.getSystemDefaultRoleList()).getOnlyValue();
        assertEquals(36, systemDefaultRole.getId().length());
        assertEquals(SystemRoleEnum.SUPER_ADMIN.getRole(), systemDefaultRole.getName());
        assertNotNull(systemDefaultRole.getCreateDate());
        assertNotNull(systemDefaultRole.getUpdateDate());
    }

}
