package com.springboot.project.test.controller.FriendshipController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.FriendshipModel;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class FriendshipControllerGetStrangerListTest extends BaseTest {

    private UserModel user;
    private UserModel friend;

    @Test
    public void test() throws URISyntaxException {
        URI url = new URIBuilder("/friendship/get_stranger_list")
                .setParameter("pageNum", String.valueOf(1))
                .setParameter("pageSize", String.valueOf(50))
                .build();
        var response = this.testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null),
                new ParameterizedTypeReference<PaginationModel<FriendshipModel>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getTotalRecord() >= 1);
        assertTrue(response.getBody().getTotalPage() >= 1);
        assertEquals(1, response.getBody().getPageNum());
        assertEquals(50, response.getBody().getPageSize());
        assertTrue(response.getBody().getList().size() >= 1);
        assertEquals(1, JinqStream.from(response.getBody().getList())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).count());
        assertEquals(this.user.getId(), JinqStream.from(response.getBody().getList())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getUser().getId());
        assertEquals(this.friend.getId(),
                JinqStream.from(response.getBody().getList())
                        .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getFriend()
                        .getId());
        assertNull(JinqStream.from(response.getBody().getList())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getCreateDate());
        assertNull(JinqStream.from(response.getBody().getList())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getUpdateDate());
        assertTrue(StringUtils
                .isNotBlank(JinqStream.from(response.getBody().getList())
                        .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getFriend()
                        .getId()));
        assertTrue(StringUtils
                .isNotBlank(JinqStream.from(response.getBody().getList())
                        .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getFriend()
                        .getUsername()));
        assertEquals(this.friend.getUsername(),
                JinqStream.from(response.getBody().getList())
                        .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getFriend()
                        .getUsername());
        assertNotNull(JinqStream.from(response.getBody().getList())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getFriend()
                .getCreateDate());
        assertNotNull(JinqStream.from(response.getBody().getList())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getFriend()
                .getUpdateDate());
        assertFalse(JinqStream.from(response.getBody().getList())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getHasInitiative());
        assertFalse(JinqStream.from(response.getBody().getList())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getIsFriend());
        assertFalse(JinqStream.from(response.getBody().getList())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getIsFriendOfFriend());
        assertFalse(JinqStream.from(response.getBody().getList())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getIsInBlacklist());
        assertFalse(JinqStream.from(response.getBody().getList())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue()
                .getIsInBlacklistOfFriend());
    }

    @BeforeEach
    public void beforeEach() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException {
        var userEmail = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        var friendEmail = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.friend = this.createAccount(friendEmail);
        this.user = this.createAccount(userEmail);
    }
}