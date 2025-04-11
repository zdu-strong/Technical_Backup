package com.springboot.project.test.service.FriendshipService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class FriendshipServiceGetFriendListTest extends BaseTest {
    private UserModel user;
    private UserModel friend;

    @Test
    public void test() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var result = this.friendshipService.getFriendList(1L, 10L, this.user.getId());
        assertEquals(1, result.getTotalRecords());
        assertEquals(this.user.getId(),
                JinqStream.from(result.getItems()).select(s -> s.getUser().getId()).getOnlyValue());
        assertEquals(this.friend.getId(),
                JinqStream.from(result.getItems()).select(s -> s.getFriend().getId()).getOnlyValue());
        assertTrue(JinqStream.from(result.getItems()).select(s -> s.getIsFriend()).getOnlyValue());
        assertFalse(JinqStream.from(result.getItems()).select(s -> s.getIsInBlacklist()).getOnlyValue());
        assertNotNull(JinqStream.from(result.getItems()).select(s -> s.getCreateDate()).getOnlyValue());
        assertNotNull(JinqStream.from(result.getItems()).select(s -> s.getUpdateDate()).getOnlyValue());
        assertTrue(StringUtils
                .isNotBlank(JinqStream.from(result.getItems()).select(s -> s.getFriend().getUsername()).getOnlyValue()));
        assertTrue(JinqStream.from(result.getItems()).select(s -> s.getIsFriend()).getOnlyValue());
        assertFalse(JinqStream.from(result.getItems()).select(s -> s.getIsInBlacklist()).getOnlyValue());
    }

    @BeforeEach
    public void beforeEach() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var userEmail = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        var friendEmail = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.user = this.createAccount(userEmail);
        this.friend = this.createAccount(friendEmail);
        this.friendshipService.addToFriendList(this.user.getId(), this.friend.getId());
    }

}
