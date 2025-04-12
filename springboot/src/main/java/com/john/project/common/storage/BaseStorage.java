package com.john.project.common.storage;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.john.project.common.CloudStorage.CloudStorageImplement;
import com.john.project.properties.DatabaseJdbcProperties;
import com.john.project.properties.StorageRootPathProperties;
import com.john.project.model.ResourceAccessLegalModel;
import com.john.project.service.EncryptDecryptService;
import com.john.project.service.StorageSpaceService;
import cn.hutool.core.util.HexUtil;

@Component
public class BaseStorage {

    @Autowired
    protected EncryptDecryptService encryptDecryptService;

    @Autowired
    protected StorageRootPathProperties storageRootPathProperties;

    @Autowired
    protected DatabaseJdbcProperties databaseJdbcProperties;

    @Autowired
    protected StorageSpaceService storageSpaceService;

    @Autowired
    protected CloudStorageImplement cloud;

    @Autowired
    protected ObjectMapper objectMapper;

    protected ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private String storageRootPath;

    public String getRootPath() {
        if (StringUtils.isBlank(storageRootPath)) {
            synchronized (getClass()) {
                if (StringUtils.isBlank(storageRootPath)) {
                    File currentFolderPath = Paths.get(new File(".").getAbsolutePath()).normalize().toFile();
                    String rootPath = "";
                    if (this.storageRootPathProperties.getStorageRootPath().equals("default")) {
                        if (new File(currentFolderPath, ".mvn").isDirectory()) {
                            rootPath = Paths.get(currentFolderPath.getAbsolutePath(), ".mvn/storage").toString();
                        } else {
                            rootPath = Paths.get(currentFolderPath.getAbsolutePath(), "storage").toString();
                        }
                    } else if (this.storageRootPathProperties.getStorageRootPath()
                            .equals("defaultTest-a56b075f-102e-edf3-8599-ffc526ec948a")) {
                        if (new File(currentFolderPath, ".mvn").isDirectory()) {
                            rootPath = Paths.get(currentFolderPath.getAbsolutePath(), "target/storage").toString();
                        } else {
                            rootPath = Paths.get(currentFolderPath.getAbsolutePath(), "storage").toString();
                        }
                    } else {
                        if (StringUtils.isBlank(this.storageRootPathProperties.getStorageRootPath().trim())) {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported storage root path");
                        }
                        if (new File(this.storageRootPathProperties.getStorageRootPath()).isAbsolute()) {
                            rootPath = this.storageRootPathProperties.getStorageRootPath();
                        } else {
                            rootPath = Paths.get(currentFolderPath.getAbsolutePath(), this.storageRootPathProperties
                                    .getStorageRootPath().replaceAll(Pattern.quote("\\"), "/")).toString();
                        }
                    }
                    rootPath = rootPath.replaceAll("\\\\", "/");
                    new File(rootPath).mkdirs();
                    this.storageRootPath = rootPath;
                }
            }
        }
        return this.storageRootPath;
    }

    @SneakyThrows
    public String getRelativePathFromRequest(HttpServletRequest request) {
        var pathSegmentList = new URIBuilder(request.getRequestURI()).getPathSegments().stream()
                .filter(s -> StringUtils.isNotBlank(s)).toList();
        if (JinqStream.from(pathSegmentList).findFirst().get().equals("resource")) {
            pathSegmentList = JinqStream.from(pathSegmentList).skip(1).toList();
        } else if (JinqStream.from(pathSegmentList).findFirst().get().equals("download")
                && JinqStream.from(pathSegmentList).skip(1).findFirst().get().equals("resource")) {
            pathSegmentList = JinqStream.from(pathSegmentList).skip(2).toList();
        } else if (JinqStream.from(pathSegmentList).findFirst().get().equals("is_directory")
                && JinqStream.from(pathSegmentList).skip(1).findFirst().get().equals("resource")) {
            pathSegmentList = JinqStream.from(pathSegmentList).skip(2).toList();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported resource path");
        }
        var encryptJsonString = Base64.getEncoder()
                .encodeToString(HexUtil.decodeHex(JinqStream.from(pathSegmentList).findFirst().get()));
        var jsonString = this.encryptDecryptService.decryptByAES(encryptJsonString);
        ResourceAccessLegalModel resourceAccessLegalModel = this.objectMapper.readValue(jsonString,
                ResourceAccessLegalModel.class);
        return this.getRelativePathFromResourcePath(
                String.join("/", Lists.asList(resourceAccessLegalModel.getRootFolderName(),
                        JinqStream.from(pathSegmentList).skip(1).toArray(String[]::new))));
    }

    @SneakyThrows
    public String getResoureUrlFromResourcePath(String relativePathOfResource) {
        String relativePath = this.getRelativePathFromResourcePath(relativePathOfResource);
        String rootFolderName = JinqStream.from(Lists.newArrayList(StringUtils.split(relativePath, "/")))
                .findFirst().get();
        ResourceAccessLegalModel resourceAccessLegalModel = new ResourceAccessLegalModel();
        resourceAccessLegalModel.setRootFolderName(rootFolderName);
        var pathSegmentList = new ArrayList<String>();
        pathSegmentList.add("resource");
        var jsonString = this.objectMapper.writeValueAsString(resourceAccessLegalModel);
        var encryptJsonString = this.encryptDecryptService.encryptWithFixedSaltByAES(jsonString);
        pathSegmentList.add(HexUtil.encodeHexStr(Base64.getDecoder().decode(encryptJsonString)));
        var pathList = JinqStream.from(Lists.newArrayList(StringUtils.split(relativePath, "/"))).toList();
        if (pathList.size() > 1) {
            pathSegmentList
                    .addAll(JinqStream.from(pathList).skip(1).toList());
        }
        var url = new URIBuilder().setPathSegments(pathSegmentList).build();
        return url.toString();
    }

    @SneakyThrows
    public String getFileNameFromResource(Resource resource) {
        if (resource instanceof UrlResource) {
            var pathSegments = Lists
                    .newArrayList(new URIBuilder(((UrlResource) resource).getURI()).getPathSegments());
            Collections.reverse(pathSegments);
            String fileName = pathSegments.stream().findFirst().get();
            return this.getRelativePathFromResourcePath(fileName);
        }
        return this.getRelativePathFromResourcePath(resource.getFilename());
    }

    protected String getRelativePathFromResourcePath(String relativePathOfResource) {
        String path = "";
        if (Paths.get(relativePathOfResource.replaceAll(Pattern.quote("\\"), "/")).isAbsolute()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only relative path can be passed in");
        } else {
            path = relativePathOfResource;
        }

        path = Paths.get(this.getRootPath(), path.replaceAll(Pattern.quote("\\"), "/")).toString();
        path = Paths.get(path).normalize().toString().replaceAll(Pattern.quote("\\"), "/");
        if (!path.startsWith(this.getRootPath())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported path");
        }
        if (path.equals(this.getRootPath())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported path");
        }
        return Paths.get(this.getRootPath()).relativize(Paths.get(path)).normalize().toString()
                .replaceAll(Pattern.quote("\\"), "/");
    }
}
