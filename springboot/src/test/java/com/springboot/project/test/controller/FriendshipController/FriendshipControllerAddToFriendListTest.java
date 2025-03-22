package com.springboot.project.test.controller.FriendshipController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.FriendshipModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class FriendshipControllerAddToFriendListTest extends BaseTest {

    private UserModel user;
    private UserModel friend;

    @Test
    public void test() throws URISyntaxException {
        URI url = new URIBuilder("/friendship/add-to-friend-list")
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
        assertTrue(response.getBody().getIsFriend());
        assertFalse(response.getBody().getIsFriendOfFriend());
        assertFalse(response.getBody().getIsInBlacklist());
        assertFalse(response.getBody().getIsInBlacklistOfFriend());
    }

    @BeforeEach
    public void beforeEach() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var userEmail = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        var friendEmail = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.friend = this.createAccount(friendEmail);
        this.user = this.createAccount(userEmail);
    }

}
