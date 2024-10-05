package com.springboot.project.common.storage;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ArrayUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.springboot.project.common.StorageResource.RandomAccessFileSystemResource;
import com.springboot.project.common.StorageResource.RangeFileSystemResource;

@Component
public class BaseStorageGetResourceForRequest extends BaseStorageDeleteResource {
    /**
     * If it is a directory, return like: JSON.toString(["childFile",
     * "childDirectory"])
     * If it is a directory, return ByteArrayStream, else not.
     * 
     * @param request
     * @return
     */
    public Resource getResourceFromRequest(HttpServletRequest request) {
        try {
            String relativePath = this.getRelativePathFromRequest(request);
            if (this.cloud.enabled()) {
                return this.cloud.getResource(relativePath);
            }
            var file = new File(this.getRootPath(), relativePath);
            if (file.isDirectory()) {
                var jsonString = this.objectMapper.writeValueAsString(getChildFileNameListFromDirectory(file));
                var jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);
                return new ByteArrayResource(jsonBytes);
            }
            return new RandomAccessFileSystemResource(file);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * If it is a directory, return like: JSON.toString(["childFile",
     * "childDirectory"])
     * If it is a directory, return ByteArrayStream, else not.
     * 
     * @return
     */
    public Resource getResourceFromRequest(HttpServletRequest request, long startIndex, long rangeContentLength) {
        try {
            String relativePath = this.getRelativePathFromRequest(request);
            if (this.cloud.enabled()) {
                return this.cloud.getResource(relativePath, startIndex, rangeContentLength);
            }
            var file = new File(this.getRootPath(), relativePath);
            if (file.isDirectory()) {
                var jsonString = this.objectMapper.writeValueAsString(getChildFileNameListFromDirectory(file));
                var jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);
                var bytes = ArrayUtils.toPrimitive(
                        JinqStream.from(Lists.newArrayList(ArrayUtils.toObject(jsonBytes))).skip(startIndex)
                                .limit(rangeContentLength)
                                .toList().toArray(new Byte[] {}));
                return new ByteArrayResource(bytes);
            }
            return new RangeFileSystemResource(file, startIndex, rangeContentLength);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private List<String> getChildFileNameListFromDirectory(File file) {
        return Lists.newArrayList(file.listFiles()).stream()
                .map((childFile) -> this.getFileNameFromResource(new FileSystemResource(childFile)))
                .toList();
    }
}
