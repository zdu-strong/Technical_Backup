package com.springboot.project.test.controller.UserMessageController;

import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;

public class UserMessageControllerSendMessageTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/user_message/send").build();
        var body = new UserMessageModel();
        body.setUser(new UserModel().setEmail("zdu.strong@gmail.com"));
        body.setContent("Hello, World!");
        var response = this.testRestTemplate.postForEntity(url, body, UserMessageModel.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody().getId());
        Assertions.assertEquals(36, response.getBody().getId().length());
        Assertions.assertEquals("Hello, World!", response.getBody().getContent());
        Assertions.assertEquals("zdu.strong@gmail.com", response.getBody().getUser().getEmail());
        Assertions.assertFalse(response.getBody().getIsDelete());
        Assertions.assertFalse(response.getBody().getIsRecall());
    }

}
