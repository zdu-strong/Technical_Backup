package com.john.project.entity;

import java.util.Date;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(indexes = {
        @Index(columnList = "executionType, createDate, id"),
        @Index(columnList = "createDate, id"),
        @Index(columnList = "executionType, status"),
})
public class DistributedExecutionMainEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String executionType;

    @Column(nullable = false)
    private Long totalPage;

    @Column(nullable = false)
    private Long totalPartition;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    @OneToMany(mappedBy = "distributedExecutionMain", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<DistributedExecutionDetailEntity> distributedExecutionDetailList;

}
