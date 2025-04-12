package com.john.project.common.storage;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jinq.tuples.Pair;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.uuid.Generators;
import cn.hutool.extra.compress.CompressUtil;
import lombok.SneakyThrows;
import static eu.ciechanowiec.sneakyfun.SneakyConsumer.sneaky;

@Component
public class BaseStorageCreateTempFile extends BaseStorageIsDirectory {

    /**
     * Create temporary files or folders based on the relative path of the resource
     * 
     * @param relativePathOfResource
     * @return
     */
    @SneakyThrows
    public File createTempFileOrFolder(String relativePathOfResource) {
        var relativePath = this.getRelativePathFromResourcePath(relativePathOfResource);
        if (this.cloud.enabled()) {
            var request = new MockHttpServletRequest();
            request.setRequestURI(this.getResoureUrlFromResourcePath(relativePath));
            var resource = this.getResourceFromRequest(request);
            if (this.isDirectory(request)) {
                var tempFolder = this.createTempFolder();
                this.writeToFolderByRelativePath(tempFolder, relativePathOfResource);
                return tempFolder;
            } else {
                return this.createTempFileOrFolder(resource);
            }
        } else {
            var tempFolder = this.createTempFolder();
            var file = new File(this.getRootPath(), relativePath);
            if (file.isDirectory()) {
                FileUtils.copyDirectory(file, tempFolder);
                return tempFolder;
            } else {
                FileUtils.copyFile(file, new File(tempFolder, file.getName()));
                return new File(tempFolder, file.getName());
            }
        }
    }

    @SneakyThrows
    public File createTempFileOrFolder(Resource resource) {
        String fileName = this.getFileNameFromResource(resource);
        if (StringUtils.isBlank(fileName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File name cannot be empty");
        }
        if (resource instanceof FileSystemResource) {
            var tempFolder = this.createTempFolder();
            var sourceFile = resource.getFile();
            if (sourceFile.isDirectory()) {
                FileUtils.copyDirectory(sourceFile, tempFolder);
                return tempFolder;
            } else {
                FileUtils.copyToDirectory(sourceFile, tempFolder);
                return new File(tempFolder, sourceFile.getName());
            }
        } else {
            File targetFile = new File(this.createTempFolder(), fileName);
            try (InputStream input = resource.getInputStream()) {
                FileUtils.copyInputStreamToFile(input, targetFile);
            }
            return targetFile;
        }
    }

    @SneakyThrows
    public File createTempFile(MultipartFile file) {
        try (InputStream input = file.getInputStream()) {
            File targetFile = new File(this.createTempFolder(), file.getOriginalFilename());
            FileUtils.copyInputStreamToFile(input, targetFile);
            return targetFile;
        }
    }

    @SneakyThrows
    public File createTempFolder() {
        var folderName = Generators.timeBasedReorderedGenerator().generate().toString();
        Optional.of(CompletableFuture.runAsync(() -> {
            this.storageSpaceService.refresh(folderName);
        }))
                .filter(s -> this.databaseJdbcProperties.getIsSupportParallelWrite())
                .ifPresent(sneaky(s -> {
                    s.get();
                }));
        File tempFolder = new File(this.getRootPath(), folderName);
        tempFolder.mkdirs();
        return tempFolder;
    }

    public File createTempFolderByDecompressingResource(Resource resource) {
        var tempFile = this.createTempFileOrFolder(resource);
        try {
            File tempFolder = this.createTempFolder();
            CompressUtil.createExtractor(StandardCharsets.UTF_8, tempFile).extract(tempFolder);
            return tempFolder;
        } finally {
            this.delete(tempFile);
        }
    }

    @SneakyThrows
    private void writeToFolderByRelativePath(File tempFolder, String relativePathOfResource) {
        var arrayDeque = new ArrayDeque<Pair<File, String>>();
        arrayDeque.add(new Pair<File, String>(tempFolder, relativePathOfResource));
        while (!arrayDeque.isEmpty()) {
            var pair = arrayDeque.pop();
            tempFolder = pair.getOne();
            relativePathOfResource = pair.getTwo();
            var relativePath = this.getRelativePathFromResourcePath(relativePathOfResource);
            var request = new MockHttpServletRequest();
            request.setRequestURI(this.getResoureUrlFromResourcePath(relativePath));
            try (var input = this.getResourceFromRequest(request).getInputStream()) {
                var jsonString = IOUtils.toString(input, StandardCharsets.UTF_8);
                var nameListOfChildFileAndChildFolder = this.objectMapper.readValue(jsonString,
                        new TypeReference<List<String>>() {

                        });
                for (var nameOfChildFileAndChildFolder : nameListOfChildFileAndChildFolder) {
                    if (nameOfChildFileAndChildFolder.endsWith("/")) {
                        var tempFolderOfChildFileAndChildFolder = new File(tempFolder, nameOfChildFileAndChildFolder);
                        tempFolderOfChildFileAndChildFolder.mkdirs();
                        arrayDeque.add(new Pair<File, String>(tempFolderOfChildFileAndChildFolder,
                                Paths.get(relativePath, nameOfChildFileAndChildFolder).toString()));
                    } else {
                        var requestOfChildFileAndChildFolder = new MockHttpServletRequest();
                        requestOfChildFileAndChildFolder.setRequestURI(this.getResoureUrlFromResourcePath(
                                Paths.get(relativePath, nameOfChildFileAndChildFolder).toString()));
                        try (var inputOfChildFileAndChildFolder = this
                                .getResourceFromRequest(requestOfChildFileAndChildFolder).getInputStream()) {
                            var tempFileOfChildFileAndChildFolder = new File(tempFolder, nameOfChildFileAndChildFolder);
                            FileUtils.copyInputStreamToFile(inputOfChildFileAndChildFolder,
                                    tempFileOfChildFileAndChildFolder);
                        }
                    }
                }
            }
        }
    }

}
