package com.john.project.test.service.FriendshipService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.john.project.model.UserModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class FriendshipServiceGetBlacklistTest extends BaseTest {
    private UserModel user;
    private UserModel friend;

    @Test
    public void test() {
        var result = this.friendshipService.getBlackList(1L, 10L, this.user.getId());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getItems().size());
        assertTrue(JinqStream.from(result.getItems()).select(s -> s.getIsInBlacklist()).getOnlyValue());
        assertFalse(JinqStream.from(result.getItems()).select(s -> s.getIsFriend()).getOnlyValue());
        assertEquals(this.friend.getId(),
                JinqStream.from(result.getItems()).select(s -> s.getFriend().getId()).getOnlyValue());
    }

    @BeforeEach
    public void beforeEach() {
        var userEmail = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        var friendEmail = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.user = this.createAccount(userEmail);
        this.friend = this.createAccount(friendEmail);
        this.friendshipService.addToBlacklist(this.user.getId(), this.friend.getId());
    }

}
