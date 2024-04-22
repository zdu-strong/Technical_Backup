package com.springboot.project.service;

import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.common.baseService.BaseService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserMessageCheckService extends BaseService {

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

    @SuppressWarnings("resource")
    public void checkCanDeleteUserMessage(String id, HttpServletRequest request) {
        var userId = this.permissionUtil.getUserId(request);
        var exists = this.UserMessageEntity()
                .where(s -> s.getId().equals(id))
                .where(s -> !s.getIsRecall())
                .leftOuterJoin((s, t) -> JinqStream.from(s.getUserMessageRelevanceList()),
                        (s, t) -> t.getUser().getId().equals(userId))
                .where(s -> s.getTwo() == null || !s.getTwo().getIsDeleted())
                .exists();
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete again");
        }
    }

}
