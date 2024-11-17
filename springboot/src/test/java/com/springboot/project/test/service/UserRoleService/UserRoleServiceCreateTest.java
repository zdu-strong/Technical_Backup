package com.springboot.project.test.service.UserRoleService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.enumerate.SystemRoleEnum;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserRoleServiceCreateTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException {
        var result = this.userRoleService.create(Generators.timeBasedReorderedGenerator().generate().toString(), List.of(SystemRoleEnum.SUPER_ADMIN), null);
        assertTrue(StringUtils.isNotBlank(result.getId()));
    }

}

