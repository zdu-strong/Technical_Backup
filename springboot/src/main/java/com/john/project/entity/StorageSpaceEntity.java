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
        @UniqueConstraint(columnNames = {"createDate", "id"})
})
public class StorageSpaceEntity {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String folderName;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

}
