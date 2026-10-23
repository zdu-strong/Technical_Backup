package com.john.project.entity;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "userId", "uniqueOneTimePasswordLogo" })
})
public class TokenEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String hashOfAccessToken;

    @Column(nullable = false)
    private String uniqueOneTimePasswordLogo;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @Column(nullable = false)
    private Boolean isDeleted;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

}
