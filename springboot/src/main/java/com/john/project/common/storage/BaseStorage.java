package com.john.project.common.storage;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.john.project.common.CloudStorage.CloudStorageInterface;
import com.john.project.common.EncryptDecryptUtil.EncryptDecryptUtil;
import com.john.project.common.uuid.UUIDUtil;
import com.john.project.properties.DevelopmentMockModeProperties;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
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
import tools.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.john.project.properties.DatabaseJdbcProperties;
import com.john.project.properties.StorageRootPathProperties;
import com.john.project.model.ResourceAccessLegalModel;
import com.john.project.service.StorageSpaceService;

@Component
public abstract class BaseStorage {

    @Autowired
    protected EncryptDecryptUtil encryptDecryptUtil;

    @Autowired
    protected StorageRootPathProperties storageRootPathProperties;

    @Autowired
    protected DevelopmentMockModeProperties developmentMockModeProperties;

    @Autowired
    protected DatabaseJdbcProperties databaseJdbcProperties;

    @Autowired
    protected StorageSpaceService storageSpaceService;

    @Autowired
    protected CloudStorageInterface cloud;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UUIDUtil uuidUtil;

    @Autowired
    private Executor applicationTaskExecutor;

    private String storageRootPath;

    public String getRootPath() {
        if (StringUtils.isBlank(storageRootPath)) {
            synchronized (getClass()) {
                if (StringUtils.isBlank(storageRootPath)) {
                    File currentFolderPath = Paths.get(new File(".").getAbsolutePath()).normalize().toFile();
                    String rootPath = "";
                    if (this.developmentMockModeProperties.getIsUnitTestEnvironment() || this.developmentMockModeProperties.getIsCypressTestEnvironment()) {
                        if (new File(currentFolderPath, ".mvn").isDirectory()) {
                            rootPath = Paths.get(currentFolderPath.getAbsolutePath(), "target/storage").toString();
                        } else {
                            rootPath = Paths.get(currentFolderPath.getAbsolutePath(), "storage").toString();
                        }
                    } else if (this.storageRootPathProperties.getStorageRootPath().equals("default")) {
                        if (new File(currentFolderPath, ".mvn").isDirectory()) {
                            rootPath = Paths.get(currentFolderPath.getAbsolutePath(), ".mvn/storage").toString();
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
        var encryptJsonString = JinqStream.from(pathSegmentList).findFirst().get();
        var jsonString = this.encryptDecryptUtil.decryptByAES(encryptJsonString);
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
        var encryptJsonString = this.encryptDecryptUtil.encryptWithFixedSaltByAES(jsonString);
        pathSegmentList.add(encryptJsonString);
        var pathList = JinqStream.from(Lists.newArrayList(StringUtils.split(relativePath, "/"))).toList();
        if (pathList.size() > 1) {
            pathSegmentList
                    .addAll(JinqStream.from(pathList).skip(1).toList());
        }
        var url = new URIBuilder().setPathSegments(pathSegmentList).build();
        return url.toString();
    }

    @SneakyThrows
    public String getFileNameFromRequest(HttpServletRequest request) {
        ArrayList<String> pathSegments = Lists
                .newArrayList(new URIBuilder(request.getRequestURI()).getPathSegments());
        Collections.reverse(pathSegments);
        String fileName = pathSegments.stream().findFirst().filter(StringUtils::isNotBlank).get();
        return fileName;
    }

    @SneakyThrows
    public String getFileNameFromResource(Resource resource) {
        if (resource instanceof UrlResource) {
            ArrayList<String> pathSegments = Lists
                    .newArrayList(new URIBuilder(resource.getURI()).getPathSegments());
            Collections.reverse(pathSegments);
            String fileName = pathSegments.stream().findFirst().filter(StringUtils::isNotBlank).get();
            return fileName;
        }
        return Optional.ofNullable(resource.getFilename()).filter(StringUtils::isNotBlank).get();
    }

    protected String getRelativePathFromResourcePath(String relativePathOfResource) {
        String path = "";
        if (Paths.get(relativePathOfResource.replaceAll(Pattern.quote("\\"), "/")).isAbsolute()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only relative path can be passed in");
        } else {
            path = relativePathOfResource;
        }

        path = Paths.get(this.getRootPath(), path).normalize().toString().replaceAll(Pattern.quote("\\"), "/");
        if (!path.startsWith(this.getRootPath())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported path");
        }
        if (path.equals(this.getRootPath())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported path");
        }
        return Paths.get(this.getRootPath()).relativize(Paths.get(path)).normalize().toString()
                .replaceAll(Pattern.quote("\\"), "/");
    }

    protected void checkHasValidOfFolderName(String folderName) {
        if (StringUtils.isBlank(folderName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Folder name cannot be empty");
        }
        if (folderName.contains("/") || folderName.contains("\\")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Folder name is invalid");
        }
        if (Paths.get(folderName).isAbsolute()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Folder name is invalid");
        }
    }

    protected String newFolderName() {
        var folderName = this.uuidUtil.v4();
        var flowable = Flowable.timer(0, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.from(applicationTaskExecutor))
                .doOnNext((s) -> this.storageSpaceService.create(folderName));
        if (this.databaseJdbcProperties.getIsSupportParallelWrite()) {
            flowable.blockingSubscribe();
        } else {
            flowable.subscribe();
        }
        return folderName;
    }
}
