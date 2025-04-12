package com.john.project.test.common.SequenceResource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.google.common.collect.Lists;
import com.john.project.common.StorageResource.RangeClassPathResource;
import com.john.project.common.StorageResource.SequenceResource;
import com.john.project.test.common.BaseTest.BaseTest;

public class SequenceResourceTest extends BaseTest {

    @Test
    public void test() {
        var sequenceResource = new SequenceResource("default.jpg", Lists.newArrayList(
                new RangeClassPathResource("image/default.jpg", 800, 5),
                new RangeClassPathResource("image/default.jpg", 805, 6)));
        assertEquals(11, sequenceResource.contentLength());
        assertEquals("default.jpg", this.storage.getFileNameFromResource(sequenceResource));
    }

}
