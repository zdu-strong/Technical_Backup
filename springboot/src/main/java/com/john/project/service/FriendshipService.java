package com.john.project.service;

import java.util.Date;

import com.john.project.entity.FriendshipEntity;
import com.john.project.entity.UserEntity;
import org.jinq.orm.stream.JinqStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.john.project.common.baseService.BaseService;
import com.john.project.entity.*;
import com.john.project.model.FriendshipModel;
import com.john.project.model.PaginationModel;

@Service
public class FriendshipService extends BaseService {

    public FriendshipModel addToFriendList(String userId, String friendId) {
        this.createFriendship(userId, friendId);

        var friendshipEntity = this.streamAll(FriendshipEntity.class)
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> s.getFriend().getId().equals(friendId))
                .where(s -> !s.getUser().getIsDeleted())
                .where(s -> !s.getFriend().getIsDeleted())
                .getOnlyValue();
        friendshipEntity.setIsInBlacklist(false);
        friendshipEntity.setIsFriend(true);
        friendshipEntity.setUpdateDate(new Date());
        this.merge(friendshipEntity);

        return this.friendshipFormatter.format(friendshipEntity);
    }

    public FriendshipModel addToBlacklist(String userId, String friendId) {
        this.createFriendship(userId, friendId);

        var friendshipEntity = this.streamAll(FriendshipEntity.class)
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> s.getFriend().getId().equals(friendId))
                .where(s -> !s.getUser().getIsDeleted())
                .where(s -> !s.getFriend().getIsDeleted())
                .getOnlyValue();
        friendshipEntity.setIsInBlacklist(true);
        friendshipEntity.setIsFriend(false);
        friendshipEntity.setUpdateDate(new Date());
        this.merge(friendshipEntity);

        return this.friendshipFormatter.format(friendshipEntity);
    }

    public void delete(String userId, String friendId) {
        var friendshipEntity = this.streamAll(FriendshipEntity.class)
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> s.getFriend().getId().equals(friendId))
                .where(s -> !s.getUser().getIsDeleted())
                .where(s -> !s.getFriend().getIsDeleted())
                .getOnlyValue();
        friendshipEntity.setIsInBlacklist(false);
        friendshipEntity.setIsFriend(false);
        friendshipEntity.setUpdateDate(new Date());
        this.merge(friendshipEntity);
    }

    @Transactional(readOnly = true)
    public FriendshipModel getFriendship(String userId, String friendId) {
        var friendshipEntity = this.streamAll(FriendshipEntity.class)
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> s.getFriend().getId().equals(friendId))
                .getOnlyValue();
        return this.friendshipFormatter.format(friendshipEntity);
    }

    @Transactional(readOnly = true)
    public PaginationModel<FriendshipModel> getFriendList(Long pageNum, Long pageSize, String userId) {
        var stream = this.streamAll(FriendshipEntity.class)
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> !s.getUser().getIsDeleted())
                .where(s -> !s.getFriend().getIsDeleted())
                .where(s -> !s.getIsInBlacklist())
                .where(s -> s.getIsFriend());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.friendshipFormatter.format(s));
    }

    @Transactional(readOnly = true)
    public PaginationModel<FriendshipModel> getStrangerList(Long pageNum, Long pageSize, String userId) {
        var userEntity = this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(userId))
                .where(s -> !s.getIsDeleted())
                .getOnlyValue();
        var stream = this.streamAll(UserEntity.class)
                .where(s -> !s.getIsDeleted())
                .where((s, t) -> !JinqStream.from(s.getReverseFridendList())
                        .where(m -> m.getUser().getId().equals(userId))
                        .where(m -> m.getIsInBlacklist() || m.getIsFriend())
                        .exists())
                .leftOuterJoin((s, t) -> JinqStream.from(s.getReverseFridendList()),
                        (s, t) -> t.getUser().getId().equals(userId));
        return new PaginationModel<>(pageNum, pageSize, stream,
                (s) -> this.friendshipFormatter.format(s.getTwo(), userEntity, s.getOne()));
    }

    @Transactional(readOnly = true)
    public PaginationModel<FriendshipModel> getBlackList(Long pageNum, Long pageSize, String userId) {
        var stream = this.streamAll(FriendshipEntity.class)
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> !s.getUser().getIsDeleted())
                .where(s -> !s.getFriend().getIsDeleted())
                .where(s -> s.getIsInBlacklist());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.friendshipFormatter.format(s));
    }

    private void createFriendship(String userId, String friendId) {
        var friendshipList = this.streamAll(FriendshipEntity.class)
                .where(s -> (s.getUser().getId().equals(userId)
                        && s.getFriend().getId().equals(friendId))
                        || (s.getUser().getId().equals(friendId)
                                && s.getFriend().getId().equals(userId)))
                .toList();
        if (friendshipList.size() == 2) {
            return;
        }
        if (friendshipList.size() == 1 && userId.equals(friendId)) {
            return;
        }

        for (var friendship : friendshipList) {
            this.remove(friendship);
        }

        {
            var user = this.streamAll(UserEntity.class)
                    .where(s -> s.getId().equals(userId))
                    .where(s -> !s.getIsDeleted())
                    .getOnlyValue();
            var friend = this.streamAll(UserEntity.class)
                    .where(s -> s.getId().equals(friendId))
                    .where(s -> !s.getIsDeleted())
                    .getOnlyValue();

            var friendshipEntity = new FriendshipEntity();
            friendshipEntity.setId(newId());
            friendshipEntity.setIsFriend(false);
            friendshipEntity.setIsInBlacklist(false);
            friendshipEntity.setCreateDate(new Date());
            friendshipEntity.setUpdateDate(new Date());
            friendshipEntity.setHasInitiative(true);
            friendshipEntity.setUser(user);
            friendshipEntity.setFriend(friend);
            this.persist(friendshipEntity);
        }

        {
            if (!userId.equals(friendId)) {
                var user = this.streamAll(UserEntity.class)
                        .where(s -> s.getId().equals(userId))
                        .where(s -> !s.getIsDeleted())
                        .getOnlyValue();
                var friend = this.streamAll(UserEntity.class)
                        .where(s -> s.getId().equals(friendId))
                        .where(s -> !s.getIsDeleted())
                        .getOnlyValue();

                var friendshipEntity = new FriendshipEntity();
                friendshipEntity.setId(newId());
                friendshipEntity.setIsFriend(false);
                friendshipEntity.setIsInBlacklist(false);
                friendshipEntity.setCreateDate(new Date());
                friendshipEntity.setUpdateDate(new Date());
                friendshipEntity.setHasInitiative(false);
                friendshipEntity.setUser(friend);
                friendshipEntity.setFriend(user);
                this.persist(friendshipEntity);
            }
        }
    }

}
