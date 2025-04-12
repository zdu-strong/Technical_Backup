package com.springboot.project.test.properties.ServerAddressProperties;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class ServerAddressPropertiesTest extends BaseTest {

    @Test
    public void test() {
        var serverAddress = this.serverAddressProperties.getServerAddress();
        assertTrue(StringUtils.isNotBlank(serverAddress));
        assertTrue(serverAddress.startsWith("http://127.0.0.1:"));
    }

}
