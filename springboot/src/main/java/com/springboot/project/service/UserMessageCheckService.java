package com.springboot.project.service;

import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserModel;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserMessageCheckService extends BaseService {

    public void checkNotNullOfUserOfUserMessage(UserMessageModel userMessageModel, HttpServletRequest request) {
        userMessageModel.setUser(new UserModel().setId(this.permissionUtil.getUserId(request)));
    }

    public void checkCannotEmptyUserMessageContent(UserMessageModel userMessageModel) {
        if (StringUtils.isBlank(userMessageModel.getUrl()) && StringUtils.isBlank(userMessageModel.getContent())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in the message content");
        }
    }

    public void checkExistsUserMessage(String id) {
        var exists = this.UserMessageEntity().where(s -> s.getId().equals(id)).exists();
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User message do not exists");
        }
    }

    public void checkCanRecallUserMessage(String id, HttpServletRequest request) {
        var userMessageEntity = this.UserMessageEntity()
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        if (!userMessageEntity.getUser().getId().equals(this.permissionUtil.getUserId(request))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only recall your own messages");
        }
        if (userMessageEntity.getIsRecall()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot recall again");
        }
    }

    public void checkCanDeleteUserMessage(String id, HttpServletRequest request) {
        var userId = this.permissionUtil.getUserId(request);
        var exists = this.UserMessageEntity()
                .where(s -> s.getId().equals(id))
                .where(s -> !s.getIsRecall())
                .leftOuterJoin((s, t) -> JinqStream.from(s.getUserMessageDeactivateList()),
                        (s, t) -> t.getUser().getId().equals(userId))
                .where(s -> s.getTwo() == null)
                .exists();
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete again");
        }
    }

}
