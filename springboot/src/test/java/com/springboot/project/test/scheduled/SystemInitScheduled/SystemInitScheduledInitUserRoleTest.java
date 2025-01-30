package com.springboot.project.test.scheduled.SystemInitScheduled;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.Test;

import com.springboot.project.enumeration.SystemPermissionEnum;
import com.springboot.project.enumeration.SystemRoleEnum;
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
        assertEquals(SystemRoleEnum.SUPER_ADMIN.name(), userRole.getName());
        assertNotNull(userRole.getCreateDate());
        assertNotNull(userRole.getUpdateDate());
        assertTrue(StringUtils.isBlank(userRole.getOrganize().getId()));
        assertEquals(1, userRole.getPermissionList().size());
        var permission = JinqStream.from(userRole.getPermissionList()).getOnlyValue();
        assertEquals(36, permission.getId().length());
        assertEquals(SystemPermissionEnum.SUPER_ADMIN_PERMISSION.name(), permission.getName());
        assertNotNull(permission.getCreateDate());
        assertNotNull(permission.getUpdateDate());
    }

}
