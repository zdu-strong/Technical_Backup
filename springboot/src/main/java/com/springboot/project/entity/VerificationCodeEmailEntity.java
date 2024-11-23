package com.springboot.project.entity;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(indexes = @Index(columnList = "email, createDate"))
@Getter
@Setter
@Accessors(chain = true)
public class VerificationCodeEmailEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String verificationCode;

    @Column(nullable = false)
    private Boolean hasUsed;

    @Column(nullable = false)
    private Boolean isPassed;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

}
