package com.springboot.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class OrganizeServiceUpdateTest extends BaseTest {

    private OrganizeModel organize;

    @Test
    public void test() {
        this.organizeService.update(organize);
        var result = this.organizeService.getById(this.organize.getId());
        assertNotNull(result.getId());
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertEquals(this.organize.getId(), result.getId());
        assertEquals("Son Goku", result.getName());
        assertEquals(0, result.getChildList().size());
        assertEquals(0, result.getChildCount());
        assertNull(result.getParent());
        assertEquals(0, result.getLevel());
    }

    @BeforeEach
    public void beforeEach() {
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        this.organize = this.organizeService.create(organizeModel);
        organize.setName("Son Goku");
    }

}
