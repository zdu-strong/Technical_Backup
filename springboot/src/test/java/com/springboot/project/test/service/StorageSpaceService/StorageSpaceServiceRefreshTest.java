package com.springboot.project.test.service.StorageSpaceService;

import java.net.URISyntaxException;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.Test;

public class StorageSpaceServiceRefreshTest extends BaseTest {
    private String folderName = Generators.timeBasedReorderedGenerator().generate().toString();

    @Test
    public void test() throws URISyntaxException {
        this.storageSpaceService.refresh(folderName);
    }

}
