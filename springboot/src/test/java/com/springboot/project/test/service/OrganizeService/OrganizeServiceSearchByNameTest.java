package com.springboot.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class OrganizeServiceSearchByNameTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        var result = this.organizeService.searchByName(1L, 20L, "Son Gohan", organizeId);
        assertEquals(1, result.getTotalRecord());
        assertEquals(1, result.getTotalPage());
        assertEquals(1, result.getPageNum());
        assertEquals(20, result.getPageSize());
        assertEquals("Son Gohan", JinqStream.from(result.getList()).select(s -> s.getName()).getOnlyValue());
        assertNotNull(JinqStream.from(result.getList()).select(s -> s.getId()).getOnlyValue());
        assertEquals(36, JinqStream.from(result.getList()).select(s -> s.getId().length()).getOnlyValue());
        assertEquals(0, JinqStream.from(result.getList()).select(s -> s.getChildList().size()).getOnlyValue());
        assertEquals(1, JinqStream.from(result.getList()).select(s -> s.getLevel()).getOnlyValue());
        assertNotNull(JinqStream.from(result.getList()).select(s -> s.getCreateDate()).getOnlyValue());
        assertNotNull(JinqStream.from(result.getList()).select(s -> s.getUpdateDate()).getOnlyValue());
        assertEquals(0, JinqStream.from(result.getList()).select(s -> s.getDescendantCount()).getOnlyValue());
        assertEquals(0, JinqStream.from(result.getList()).select(s -> s.getChildCount()).getOnlyValue());
    }

    @BeforeEach
    public void beforeEach() {
        var parentOrganizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        var parentOrganize = this.organizeService.create(parentOrganizeModel);
        this.organizeUtil.refresh(parentOrganize.getId());
        var childOrganizeModel = new OrganizeModel().setName("Son Gohan").setParent(parentOrganize);
        var childOrganize = this.organizeService.create(childOrganizeModel);
        this.organizeUtil.refresh(childOrganize.getId());
        this.organizeId = parentOrganize.getId();
    }

}
