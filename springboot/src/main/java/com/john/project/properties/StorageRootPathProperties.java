package com.john.project.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class StorageRootPathProperties {

    @Value("${properties.storage.root.path}")
    private String storageRootPath;

}
