package com.springboot.project.test.service.FriendshipService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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

public class FriendshipServiceGetStrangerListTest extends BaseTest {
    private UserModel user;
    private UserModel friend;

    @Test
    public void test() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var result = this.friendshipService.getStrangerList(1L, 50L, this.user.getId())
                .getItems()
                .stream()
                .filter(s -> s.getFriend().getId().equals(this.friend.getId())).toList();
        assertEquals(1, result.size());
        assertEquals(user.getId(), JinqStream.from(result).select(s -> s.getUser().getId()).getOnlyValue());
        assertEquals(friend.getId(),
                JinqStream.from(result).select(s -> s.getFriend().getId()).getOnlyValue());
        assertNull(JinqStream.from(result).select(s -> s.getCreateDate()).getOnlyValue());
        assertNull(JinqStream.from(result).select(s -> s.getUpdateDate()).getOnlyValue());
        assertTrue(StringUtils
                .isNotBlank(JinqStream.from(result).select(s -> s.getFriend().getUsername()).getOnlyValue()));
        assertFalse(JinqStream.from(result).select(s -> s.getIsFriend()).getOnlyValue());
        assertFalse(JinqStream.from(result).select(s -> s.getIsInBlacklist()).getOnlyValue());
    }

    @BeforeEach
    public void beforeEach() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var userEmail = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        var friendEmail = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.user = this.createAccount(userEmail);
        this.friend = this.createAccount(friendEmail);
    }

}
