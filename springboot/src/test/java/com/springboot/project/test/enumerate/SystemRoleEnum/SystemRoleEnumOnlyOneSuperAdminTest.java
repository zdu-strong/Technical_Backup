package com.springboot.project.test.enumerate.SystemRoleEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import com.springboot.project.enumerate.SystemRoleEnum;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class SystemRoleEnumOnlyOneSuperAdminTest extends BaseTest {

    @Test
    public void test() {
        assertEquals(1, Arrays.stream(SystemRoleEnum.values()).filter(s -> s.getIsSuperAdmin()).count());
    }

}
