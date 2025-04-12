package com.john.project.test.controller.FriendshipController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import lombok.SneakyThrows;
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
import com.john.project.model.FriendshipModel;
import com.john.project.model.PaginationModel;
import com.john.project.model.UserModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class FriendshipControllerGetBlacklistTest extends BaseTest {

    private UserModel user;
    private UserModel friend;

    @Test
    @SneakyThrows
    public void test() {
        URI url = new URIBuilder("/friendship/get-blacklist")
                .setParameter("pageNum", String.valueOf(1))
                .setParameter("pageSize", String.valueOf(20))
                .build();
        var response = this.testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null),
                new ParameterizedTypeReference<PaginationModel<FriendshipModel>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalRecords());
        assertEquals(1, response.getBody().getTotalPages());
        assertEquals(1, response.getBody().getPageNum());
        assertEquals(20, response.getBody().getPageSize());
        assertEquals(1, response.getBody().getItems().size());
        assertEquals(this.user.getId(), JinqStream.from(response.getBody().getItems()).getOnlyValue().getUser().getId());
        assertEquals(this.friend.getId(),
                JinqStream.from(response.getBody().getItems()).getOnlyValue().getFriend().getId());
        assertNotNull(JinqStream.from(response.getBody().getItems()).getOnlyValue().getCreateDate());
        assertNotNull(JinqStream.from(response.getBody().getItems()).getOnlyValue().getUpdateDate());
        assertTrue(StringUtils
                .isNotBlank(JinqStream.from(response.getBody().getItems()).getOnlyValue().getFriend().getId()));
        assertTrue(StringUtils
                .isNotBlank(JinqStream.from(response.getBody().getItems()).getOnlyValue().getFriend().getUsername()));
        assertEquals(this.friend.getUsername(),
                JinqStream.from(response.getBody().getItems()).getOnlyValue().getFriend().getUsername());
        assertNotNull(JinqStream.from(response.getBody().getItems()).getOnlyValue().getFriend().getCreateDate());
        assertNotNull(JinqStream.from(response.getBody().getItems()).getOnlyValue().getFriend().getUpdateDate());
        assertTrue(JinqStream.from(response.getBody().getItems()).getOnlyValue().getHasInitiative());
        assertFalse(JinqStream.from(response.getBody().getItems()).getOnlyValue().getIsFriend());
        assertFalse(JinqStream.from(response.getBody().getItems()).getOnlyValue().getIsFriendOfFriend());
        assertTrue(JinqStream.from(response.getBody().getItems()).getOnlyValue().getIsInBlacklist());
        assertFalse(JinqStream.from(response.getBody().getItems()).getOnlyValue().getIsInBlacklistOfFriend());
    }

    @BeforeEach
    @SneakyThrows
    public void beforeEach() {
        var userEmail = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        var friendEmail = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.friend = this.createAccount(friendEmail);
        this.user = this.createAccount(userEmail);
        URI url = new URIBuilder("/friendship/add-to-blacklist")
                .setParameter("friendId", this.friend.getId())
                .build();
        var response = this.testRestTemplate.postForEntity(url, null, FriendshipModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(StringUtils.isNotBlank(response.getBody().getId()));
        assertEquals(this.user.getId(), response.getBody().getUser().getId());
        assertEquals(this.friend.getId(), response.getBody().getFriend().getId());
        assertNotNull(response.getBody().getCreateDate());
        assertNotNull(response.getBody().getUpdateDate());
        assertTrue(StringUtils.isNotBlank(response.getBody().getFriend().getId()));
        assertTrue(StringUtils.isNotBlank(response.getBody().getFriend().getUsername()));
        assertEquals(this.friend.getUsername(), response.getBody().getFriend().getUsername());
        assertNotNull(response.getBody().getFriend().getCreateDate());
        assertNotNull(response.getBody().getFriend().getUpdateDate());
        assertTrue(response.getBody().getHasInitiative());
        assertFalse(response.getBody().getIsFriend());
        assertFalse(response.getBody().getIsFriendOfFriend());
        assertTrue(response.getBody().getIsInBlacklist());
        assertFalse(response.getBody().getIsInBlacklistOfFriend());
    }
}