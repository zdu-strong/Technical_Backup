package com.john.project.entity;

import java.util.Date;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "distributedExecutionMainId", "pageNum" }),
        @UniqueConstraint(columnNames = {
                "distributedExecutionMainId", "partitionNum", "pageNum" })
}, indexes = {
        @Index(columnList = "distributedExecutionMainId, hasError"),
})
@Getter
@Setter
@Accessors(chain = true)
public class DistributedExecutionDetailEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private Long pageNum;

    @Column(nullable = false)
    private Long partitionNum;

    /**
     * Whether an exception occurs
     */
    @Column(nullable = false)
    private Boolean hasError;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    private DistributedExecutionMainEntity distributedExecutionMain;
}
