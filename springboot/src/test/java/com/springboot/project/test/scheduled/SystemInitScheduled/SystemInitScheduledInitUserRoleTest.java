package com.springboot.project.test.scheduled.SystemInitScheduled;

import static org.junit.jupiter.api.Assertions.*;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.Test;
import com.springboot.project.enums.SystemPermissionEnum;
import com.springboot.project.enums.SystemRoleEnum;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class SystemInitScheduledInitUserRoleTest extends BaseTest {

    @Test
    public void test() {
        this.systemInitScheduled.scheduled();
        var paginationModel = this.userRoleRelationService.searchUserRoleForSuperAdminByPagination(1, SystemRoleEnum.values().length);
        var roleList = paginationModel.getItems();
        assertEquals(1, roleList.size());
        var roleModel = JinqStream.from(roleList).getOnlyValue();
        assertEquals(36, roleModel.getId().length());
        assertEquals(SystemRoleEnum.SUPER_ADMIN.getValue(), roleModel.getName());
        assertNotNull(roleModel.getCreateDate());
        assertNotNull(roleModel.getUpdateDate());
        assertTrue(roleModel.getOrganizeList().isEmpty());
        assertEquals(1, roleModel.getPermissionList().size());
        var permission = JinqStream.from(roleModel.getPermissionList()).getOnlyValue();
        assertEquals(SystemPermissionEnum.SUPER_ADMIN.getValue(), permission);
    }

}
