package com.springboot.project.common.storage;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.uuid.Generators;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.common.StorageResource.SequenceResource;
import com.springboot.project.model.StorageFileModel;
import lombok.SneakyThrows;

@Component
public class BaseStorageSave extends BaseStorageCreateTempFile {

    @SneakyThrows
    public StorageFileModel storageResource(Resource resource) {
        var storageFileModel = new StorageFileModel()
                .setFolderName(Generators.timeBasedReorderedGenerator().generate().toString());
        this.storageSpaceService.refresh(storageFileModel.getFolderName());

        /* Set file name */
        storageFileModel.setFileName(this.getFileNameFromResource(resource));
        if (resource.isFile() && resource.getFile().isDirectory()) {
            storageFileModel.setFileName(null);
        }

        /* Set directory size */
        if (resource.isFile()) {
            File sourceFile = resource.getFile();
            if (!sourceFile.exists()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource does not exist");
            }
            if (sourceFile.isDirectory()) {
                storageFileModel.setFolderSize(FileUtils.sizeOfDirectory(sourceFile));
            } else {
                storageFileModel.setFolderSize(resource.contentLength());
            }
        } else {
            storageFileModel.setFolderSize(resource.contentLength());
        }

        /* Get relative path */
        String relativePath = this.getRelativePathFromResourcePath(storageFileModel.getFolderName());
        if (StringUtils.isNotBlank(storageFileModel.getFileName())) {
            relativePath = this.getRelativePathFromResourcePath(
                    Paths.get(storageFileModel.getFolderName(), storageFileModel.getFileName()).toString());
        }

        /* Set relative path */
        storageFileModel.setRelativePath(relativePath);

        /* Set relative url */
        storageFileModel.setRelativeUrl(this.getResoureUrlFromResourcePath(relativePath));

        /* Set relative download url */
        storageFileModel.setRelativeDownloadUrl("/download" + storageFileModel.getRelativeUrl());

        if (resource.isFile()) {
            File sourceFile = resource.getFile();
            if (!sourceFile.exists()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource does not exist");
            }
            if (this.cloud.enabled()) {
                this.cloud.storageResource(sourceFile, storageFileModel.getRelativePath());
            } else {
                if (sourceFile.isDirectory()) {
                    FileUtils.copyDirectory(sourceFile,
                            new File(this.getRootPath(), storageFileModel.getRelativePath()));
                } else {
                    FileUtils.copyFile(sourceFile,
                            new File(this.getRootPath(), storageFileModel.getRelativePath()));
                }
            }
        } else {
            if (this.cloud.enabled()) {
                if (resource instanceof SequenceResource) {
                    this.cloud.storageResource((SequenceResource) resource, relativePath);
                } else {
                    this.cloud.storageResource(new SequenceResource(this.getFileNameFromResource(resource),
                            Lists.newArrayList(resource)), relativePath);
                }
            } else {
                try (var input = resource.getInputStream()) {
                    FileUtils.copyToFile(input, new File(this.getRootPath(), storageFileModel.getRelativePath()));
                }
            }
        }
        return storageFileModel;
    }

    public StorageFileModel storageResource(MultipartFile file) {
        var tempFile = this.createTempFile(file);
        var storageFileModel = new StorageFileModel()
                .setFolderName(tempFile.getParentFile().getName());
        storageFileModel.setFolderSize(file.getSize());
        storageFileModel.setFileName(file.getOriginalFilename());
        storageFileModel.setRelativePath(this.getRelativePathFromResourcePath(
                Paths.get(storageFileModel.getFolderName(), storageFileModel.getFileName()).toString()));
        storageFileModel.setRelativeUrl(this.getResoureUrlFromResourcePath(storageFileModel.getRelativePath()));
        storageFileModel.setRelativeDownloadUrl("/download" + storageFileModel.getRelativePath());
        if (this.cloud.enabled()) {
            this.cloud.storageResource(tempFile, storageFileModel.getRelativePath());
            this.delete(tempFile);
        }
        return storageFileModel;
    }

    public StorageFileModel storageUrl(String url) {
        var mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setRequestURI(url);
        var relativePath = this.getRelativePathFromRequest(mockHttpServletRequest);
        var list = Lists.newArrayList(StringUtils.split(relativePath, "/"));
        var storageFileModel = new StorageFileModel().setFolderName(
                JinqStream.from(list).findFirst().get());
        storageFileModel.setFolderSize(this.getResourceSizeByRelativePath(relativePath));
        storageFileModel.setFileName(JinqStream.from(list).skip(list.size() > 1 ? list.size() - 1 : 1)
                .findFirst().orElse(null));
        storageFileModel.setRelativePath(relativePath);

        /* Set relative url */
        storageFileModel.setRelativeUrl(this.getResoureUrlFromResourcePath(relativePath));

        /* Set relative download url */
        storageFileModel.setRelativeDownloadUrl("/download" + storageFileModel.getRelativeUrl());
        return storageFileModel;
    }

    public StorageFileModel storageUrlAsFolderAfterDecompressing(String url) {
        var mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setRequestURI(url);
        var tempFolder = this
                .createTempFolderByDecompressingResource(this.getResourceFromRequest(mockHttpServletRequest));
        var relativePath = tempFolder.getName();
        var storageFileModel = new StorageFileModel().setFolderName(tempFolder.getName());
        storageFileModel.setFolderSize(FileUtils.sizeOfDirectory(tempFolder));
        storageFileModel.setFileName(null);
        storageFileModel.setRelativePath(relativePath);

        /* Set relative url */
        storageFileModel.setRelativeUrl(this.getResoureUrlFromResourcePath(relativePath));

        /* Set relative download url */
        storageFileModel.setRelativeDownloadUrl("/download" + storageFileModel.getRelativeUrl());
        if (this.cloud.enabled()) {
            this.cloud.storageResource(tempFolder, relativePath);
            this.delete(tempFolder);
        }
        return storageFileModel;
    }

    @SneakyThrows
    private Long getResourceSizeByRelativePath(String relativePathOfResource) {
        var relativePath = this.getRelativePathFromResourcePath(relativePathOfResource);
        var request = new MockHttpServletRequest();
        request.setRequestURI(this.getResoureUrlFromResourcePath(relativePath));
        var resource = this.getResourceFromRequest(request);
        if (this.cloud.enabled()) {
            if (resource instanceof ByteArrayResource) {
                try (var input = resource.getInputStream()) {
                    var jsonString = IOUtils.toString(input, StandardCharsets.UTF_8);
                    var nameListOfChildFileAndChildFolder = this.objectMapper.readValue(jsonString,
                            new TypeReference<List<String>>() {
                            });
                    return JinqStream.from(nameListOfChildFileAndChildFolder)
                            .select(nameOfChildFileAndChildFolder -> this.getResourceSizeByRelativePath(
                                    Paths.get(relativePath, nameOfChildFileAndChildFolder).toString()))
                            .sumLong(s -> s);
                }
            } else {
                return resource.contentLength();
            }
        } else {
            var file = new File(this.getRootPath(), relativePath);
            if (file.isDirectory()) {
                return FileUtils.sizeOfDirectory(new File(this.getRootPath(), relativePath));
            } else {
                return file.length();
            }
        }
    }

}
