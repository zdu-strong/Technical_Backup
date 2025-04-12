package com.springboot.project.test.service.RoleService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.enums.SystemPermissionEnum;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class RoleServiceCreateTest extends BaseTest {

    @Test
    public void test() {
        var result = this.roleService.create(Generators.timeBasedReorderedGenerator().generate().toString(), List.of(SystemPermissionEnum.SUPER_ADMIN), List.of());
        assertTrue(StringUtils.isNotBlank(result.getId()));
    }

}

