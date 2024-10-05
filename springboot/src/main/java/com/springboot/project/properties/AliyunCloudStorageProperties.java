package com.springboot.project.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;

@Component
@Getter
public class AliyunCloudStorageProperties {

    @Value("${properties.storage.cloud.aliyun.enabled}")
    private Boolean enabled;

    @Value("${properties.storage.cloud.aliyun.endpoint}")
    private String endpoint;

    @Value("${properties.storage.cloud.aliyun.bucketName}")
    private String bucketName;

    @Value("${properties.storage.cloud.aliyun.accessKeyId}")
    private String accessKeyId;

    @Value("${properties.storage.cloud.aliyun.accessKeySecret}")
    private String accessKeySecret;

}
