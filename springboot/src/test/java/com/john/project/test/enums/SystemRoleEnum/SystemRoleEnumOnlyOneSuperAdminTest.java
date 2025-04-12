package com.john.project.test.enums.SystemRoleEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

import com.john.project.enums.SystemPermissionEnum;
import com.john.project.test.common.BaseTest.BaseTest;

public class SystemRoleEnumOnlyOneSuperAdminTest extends BaseTest {

    @Test
    public void test() {
        assertEquals(1, Arrays.stream(SystemPermissionEnum.values()).filter(s -> s.getIsSuperAdmin()).count());
    }

}
