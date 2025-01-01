package com.springboot.project.test.scheduled.SystemInitScheduled;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.Test;
import com.springboot.project.enumerate.SystemPermissionEnum;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class SystemInitScheduledInitUserRoleTest extends BaseTest {

    @Test
    public void test() {
        this.systemInitScheduled.scheduled();
        var paginationModel = this.roleService.searchUserRoleForSuperAdminByPagination(1, 200);
        var userRoleList = paginationModel.getList();
        assertEquals(1, userRoleList.size());
        var userRole = JinqStream.from(userRoleList).getOnlyValue();
        assertEquals(36, userRole.getId().length());
        assertEquals(SystemPermissionEnum.SUPER_ADMIN_PERMISSION.name(), userRole.getName());
        assertNotNull(userRole.getCreateDate());
        assertNotNull(userRole.getUpdateDate());
        assertTrue(StringUtils.isBlank(userRole.getOrganize().getId()));
        assertEquals(1, userRole.getPermissionList().size());
        var systemRole = JinqStream.from(userRole.getPermissionList()).getOnlyValue();
        assertEquals(36, systemRole.getId().length());
        assertEquals(SystemPermissionEnum.SUPER_ADMIN_PERMISSION.name(), systemRole.getName());
        assertNotNull(systemRole.getCreateDate());
        assertNotNull(systemRole.getUpdateDate());
    }

}
