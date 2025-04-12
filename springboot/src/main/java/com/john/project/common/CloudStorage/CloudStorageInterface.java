package com.john.project.common.CloudStorage;

import java.io.File;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.john.project.common.StorageResource.SequenceResource;

@Component
public interface CloudStorageInterface {

    boolean enabled();

    void storageResource(File sourceFileOrSourceFolder, String key);

    void storageResource(SequenceResource sourceFile, String key);

    void delete(String key);

    /**
     * If it is a directory, return like: JSON.toString(["childFile", "childDirectory"])
     * If it is a directory, return ByteArrayStream, else CloudStorageUrlResource.
     * @param key
     * @return
     */
    Resource getResource(String key);

    /**
     * If it is a directory, return like: JSON.toString(["childFile", "childDirectory"])
     * If it is a directory, return ByteArrayStream. else RangeCloudStorageUrlResource.
     * @param key
     * @param startIndex
     * @param rangeContentLength
     * @return
     */
    Resource getResource(String key, long startIndex, long rangeContentLength);
}