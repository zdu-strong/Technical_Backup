package com.springboot.project.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.springboot.project.SpringbootProjectApplication;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class SpringbootProjectApplicationTest extends BaseTest {

    @Test
    public void test() {
        assertNotNull(SpringbootProjectApplication.class, "Startup class does not exist");
    }

}
