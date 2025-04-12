package com.john.project.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.john.project.SpringbootProjectApplication;
import com.john.project.test.common.BaseTest.BaseTest;

public class SpringbootProjectApplicationTest extends BaseTest {

    @Test
    public void test() {
        assertNotNull(SpringbootProjectApplication.class, "Startup class does not exist");
    }

}
