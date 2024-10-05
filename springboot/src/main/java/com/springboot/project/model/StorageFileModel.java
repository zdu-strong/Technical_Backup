package com.springboot.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class StorageFileModel {
    private String folderName;

    /**
     * When storing a folder, fileName is null. When storing a file, fileName cannot be null, nor can it be an empty string.
     */
    private String fileName;

    private Long folderSize;

    private String relativePath;

    private String relativeUrl;

    private String relativeDownloadUrl;

    public StorageFileModel() {

    }

    public StorageFileModel(String folderName, String fileName) {
        this.folderName = folderName;
        this.fileName = fileName;
    }

    public StorageFileModel(String folderName) {
        this.folderName = folderName;
    }

}
