package com.springboot.project.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(indexes = @Index(columnList = "folderName"))
@Getter
@Setter
@Accessors(chain = true)
public class UserMessageEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    private String content;

    @Column(nullable = false)
    private Boolean isRecall;

    @Column(nullable = false)
    private String folderName;

    @Column(nullable = false)
    private Long folderSize;

    @Column(nullable = false, length = 1024 * 1024 * 1024)
    @Lob
    private String fileName;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    @OneToMany(mappedBy = "userMessage", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<UserMessageDeactivateEntity> userMessageDeactivateList;

}
