package com.springboot.project.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class UserEntity {

    @Id
    private String id;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    private String username;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<UserEmailEntity> userEmailList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<UserMessageEntity> userMessageList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<TokenEntity> tokenList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<FriendshipEntity> fridendList;

    @OneToMany(mappedBy = "friend", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<FriendshipEntity> reverseFridendList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<UserMessageDeactivateEntity> userMessageDeactivateList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<UserRoleRelationEntity> userRoleRelationList;

}
