package com.springboot.project.test.scheduled.SystemInitScheduled;

import static org.junit.jupiter.api.Assertions.*;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.Test;
import com.springboot.project.enumerate.SystemRoleEnum;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class SystemInitScheduledInitUserRoleTest extends BaseTest {

    @Test
    public void test() {
        this.systemInitScheduled.scheduled();
        var paginationModel = this.userRoleService.searchUserRoleForSuperAdminByPagination(1, 200);
        var userRoleList = paginationModel.getList();
        assertEquals(1, userRoleList.size());
        var userRole = JinqStream.from(userRoleList).getOnlyValue();
        assertEquals(36, userRole.getId().length());
        assertEquals(SystemRoleEnum.SUPER_ADMIN.getRole(), userRole.getName());
        assertNotNull(userRole.getCreateDate());
        assertNotNull(userRole.getUpdateDate());
        assertNull(userRole.getOrganize());
        assertEquals(1, userRole.getSystemRoleList().size());
        var systemRole = JinqStream.from(userRole.getSystemRoleList()).getOnlyValue();
        assertEquals(36, systemRole.getId().length());
        assertEquals(SystemRoleEnum.SUPER_ADMIN.getRole(), systemRole.getName());
        assertNotNull(systemRole.getCreateDate());
        assertNotNull(systemRole.getUpdateDate());
    }

}
