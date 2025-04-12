package com.john.project.test.controller.FriendshipController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

public class FriendshipControllerGetStrangerListTest extends BaseTest {

    private UserModel user;
    private UserModel friend;

    @Test
    @SneakyThrows
    public void test() {
        URI url = new URIBuilder("/friendship/get-stranger-list")
                .setParameter("pageNum", String.valueOf(1))
                .setParameter("pageSize", String.valueOf(50))
                .build();
        var response = this.testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null),
                new ParameterizedTypeReference<PaginationModel<FriendshipModel>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getTotalRecords() >= 1);
        assertTrue(response.getBody().getTotalPages() >= 1);
        assertEquals(1, response.getBody().getPageNum());
        assertEquals(50, response.getBody().getPageSize());
        assertTrue(response.getBody().getItems().size() >= 1);
        assertEquals(1, JinqStream.from(response.getBody().getItems())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).count());
        assertEquals(this.user.getId(), JinqStream.from(response.getBody().getItems())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getUser().getId());
        assertEquals(this.friend.getId(),
                JinqStream.from(response.getBody().getItems())
                        .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getFriend()
                        .getId());
        assertNull(JinqStream.from(response.getBody().getItems())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getCreateDate());
        assertNull(JinqStream.from(response.getBody().getItems())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getUpdateDate());
        assertTrue(StringUtils
                .isNotBlank(JinqStream.from(response.getBody().getItems())
                        .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getFriend()
                        .getId()));
        assertTrue(StringUtils
                .isNotBlank(JinqStream.from(response.getBody().getItems())
                        .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getFriend()
                        .getUsername()));
        assertEquals(this.friend.getUsername(),
                JinqStream.from(response.getBody().getItems())
                        .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getFriend()
                        .getUsername());
        assertNotNull(JinqStream.from(response.getBody().getItems())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getFriend()
                .getCreateDate());
        assertNotNull(JinqStream.from(response.getBody().getItems())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getFriend()
                .getUpdateDate());
        assertFalse(JinqStream.from(response.getBody().getItems())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getHasInitiative());
        assertFalse(JinqStream.from(response.getBody().getItems())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getIsFriend());
        assertFalse(JinqStream.from(response.getBody().getItems())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getIsFriendOfFriend());
        assertFalse(JinqStream.from(response.getBody().getItems())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue().getIsInBlacklist());
        assertFalse(JinqStream.from(response.getBody().getItems())
                .where(s -> s.getFriend().getId().equals(this.friend.getId())).getOnlyValue()
                .getIsInBlacklistOfFriend());
    }

    @BeforeEach
    public void beforeEach() {
        var userEmail = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        var friendEmail = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.friend = this.createAccount(friendEmail);
        this.user = this.createAccount(userEmail);
    }
}