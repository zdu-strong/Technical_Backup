package com.springboot.project.common.CloudStorage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ListObjectsV2Request;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.OSSObjectSummary;
import com.google.common.collect.Lists;
import com.springboot.project.common.StorageResource.CloudStorageUrlResource;
import com.springboot.project.common.StorageResource.RangeCloudStorageUrlResource;
import com.springboot.project.common.StorageResource.SequenceResource;
import com.springboot.project.properties.AliyunCloudStorageProperties;
import lombok.SneakyThrows;
import com.springboot.project.common.storage.BaseStorage;

@Component
public class AliyunCloudStorage extends BaseStorage implements CloudStorageInterface {

    private Duration tempUrlSurvivalDuration = Duration.ofDays(7);

    @Autowired
    private AliyunCloudStorageProperties aliyunCloudStorageProperties;

    @Override
    public boolean enabled() {
        return this.aliyunCloudStorageProperties.getEnabled();
    }

    @Override
    @SneakyThrows
    public void storageResource(File sourceFileOrSourceFolder, String key) {
        var ossClient = this.getOssClientClient();
        try {
            if (sourceFileOrSourceFolder.isDirectory()) {
                key += "/";
                ossClient.putObject(this.aliyunCloudStorageProperties.getBucketName(), key,
                        new ByteArrayInputStream(new byte[] {}));
                for (var childOfSourceFileOrSourceFolder : sourceFileOrSourceFolder.listFiles()) {
                    this.storageResource(childOfSourceFileOrSourceFolder,
                            key + this.getFileNameFromResource(
                                    new FileSystemResource(childOfSourceFileOrSourceFolder)));
                }
            } else {
                try (var input = new FileSystemResource(sourceFileOrSourceFolder).getInputStream()) {
                    ossClient.putObject(this.aliyunCloudStorageProperties.getBucketName(), key,
                            input);
                }
            }
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    @SneakyThrows
    public void storageResource(SequenceResource sourceFile, String key) {
        var ossClient = this.getOssClientClient();
        try {
            try (var input = sourceFile.getInputStream()) {
                ossClient.putObject(this.aliyunCloudStorageProperties.getBucketName(), key, input);
            }
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public void delete(String key) {
        var ossClient = this.getOssClientClient();
        try {
            var list = this.getList(key + "/");
            if (!list.isEmpty()) {
                JinqStream.from(list)
                        .where(s -> !s.equals(key + "/"))
                        .select(s -> this.getFileNameFromResource(new FileSystemResource(s)))
                        .select(s -> {
                            this.delete(key + "/" + s);
                            return "";
                        }).toList();
                ossClient.deleteObject(this.aliyunCloudStorageProperties.getBucketName(), key + "/");
            }
            ossClient.deleteObject(this.aliyunCloudStorageProperties.getBucketName(), key);
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    @SneakyThrows
    public Resource getResource(String key) {
        var list = this.getList(key + "/");
        if (!list.isEmpty()) {
            var jsonString = this.objectMapper
                    .writeValueAsString(JinqStream.from(list).where(s -> !s.equals(key + "/"))
                            .select(s -> this.getFileNameFromResource(new FileSystemResource(s))).toList());
            var jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);
            return new ByteArrayResource(jsonBytes);
        }

        var expireDate = DateUtils.addMilliseconds(new Date(),
                Long.valueOf(this.tempUrlSurvivalDuration.toMillis()).intValue());

        var ossClient = this.getOssClientClient();
        try {
            var url = ossClient.generatePresignedUrl(this.aliyunCloudStorageProperties.getBucketName(), key,
                    expireDate);
            var getObjectRequest = new GetObjectRequest(this.aliyunCloudStorageProperties.getBucketName(),
                    key);
            var ossObject = ossClient.getObject(getObjectRequest);
            return new CloudStorageUrlResource(url, ossObject.getResponse().getContentLength());
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    @SneakyThrows
    public Resource getResource(String key, long startIndex, long rangeContentLength) {
        var list = this.getList(key + "/");
        if (!list.isEmpty()) {
            var jsonString = this.objectMapper
                    .writeValueAsString(JinqStream.from(list).where(s -> !s.equals(key + "/"))
                            .select(s -> this.getFileNameFromResource(new FileSystemResource(s))).toList());
            var jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);
            var bytes = ArrayUtils.toPrimitive(
                    JinqStream.from(Lists.newArrayList(ArrayUtils.toObject(jsonBytes))).skip(startIndex)
                            .limit(rangeContentLength)
                            .toList().toArray(new Byte[] {}));
            return new ByteArrayResource(bytes);
        }

        var expireDate = DateUtils.addMilliseconds(new Date(),
                Long.valueOf(this.tempUrlSurvivalDuration.toMillis()).intValue());

        var ossClient = this.getOssClientClient();
        try {
            var url = ossClient.generatePresignedUrl(this.aliyunCloudStorageProperties.getBucketName(), key,
                    expireDate);
            return new RangeCloudStorageUrlResource(url, startIndex, rangeContentLength);
        } finally {
            ossClient.shutdown();
        }
    }

    private List<String> getList(String prefix) {
        String nextContinuationToken = null;
        ListObjectsV2Result result = null;
        var list = new HashSet<String>();

        while (true) {
            if (result != null && !result.isTruncated()) {
                break;
            }

            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request(
                    this.aliyunCloudStorageProperties.getBucketName()).withMaxKeys(200);
            listObjectsV2Request.setPrefix(prefix);
            listObjectsV2Request.setDelimiter("/");
            listObjectsV2Request.setContinuationToken(nextContinuationToken);
            var ossClient = this.getOssClientClient();
            try {
                result = ossClient.listObjectsV2(listObjectsV2Request);
            } finally {
                ossClient.shutdown();
            }
            for (OSSObjectSummary objectSummary : result.getObjectSummaries()) {
                list.add(objectSummary.getKey());
            }

            for (String commonPrefix : result.getCommonPrefixes()) {
                list.add(commonPrefix);
            }

            nextContinuationToken = result.getNextContinuationToken();
        }
        return list.stream().toList();
    }

    private OSS getOssClientClient() {
        var ossClient = new OSSClientBuilder().build(this.aliyunCloudStorageProperties.getEndpoint(),
                this.aliyunCloudStorageProperties.getAccessKeyId(),
                this.aliyunCloudStorageProperties.getAccessKeySecret());
        return ossClient;
    }

}
