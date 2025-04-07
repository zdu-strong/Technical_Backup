package com.springboot.project.common.CloudStorage;

import java.io.File;
import java.util.Optional;
import cn.hutool.extra.spring.SpringUtil;
import org.jinq.orm.stream.JinqStream;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.springboot.project.common.StorageResource.SequenceResource;

@Component
public class CloudStorageImplement implements CloudStorageInterface {

    @Override
    public boolean enabled() {
        return this.getCloudOptional().isPresent();
    }

    @Override
    public void storageResource(File sourceFileOrSourceFolder, String key) {
        this.getCloud().storageResource(sourceFileOrSourceFolder, key);
    }

    @Override
    public void storageResource(SequenceResource sourceFile, String key) {
        this.getCloud().storageResource(sourceFile, key);
    }

    @Override
    public void delete(String key) {
        this.getCloud().delete(key);
    }

    @Override
    public Resource getResource(String key) {
        return this.getCloud().getResource(key);
    }

    @Override
    public Resource getResource(String key, long startIndex, long rangeContentLength) {
        return this.getCloud().getResource(key, startIndex, rangeContentLength);
    }

    private Optional<CloudStorageInterface> getCloudOptional() {
        return JinqStream.from(SpringUtil.getBeansOfType(CloudStorageInterface.class).values())
                .where(s -> !(s instanceof CloudStorageImplement))
                .where(s -> s.enabled())
                .findOne();
    }

    private CloudStorageInterface getCloud() {
        return this.getCloudOptional().get();
    }

}
