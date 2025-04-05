package com.springboot.project.common.CloudStorage;

import java.io.File;
import java.util.List;
import cn.hutool.extra.spring.SpringUtil;
import org.jinq.orm.stream.JinqStream;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.google.common.collect.Lists;
import com.springboot.project.common.StorageResource.SequenceResource;

@Component
public class CloudStorageImplement implements CloudStorageInterface {

    @Override
    public boolean enabled() {
        return JinqStream.from(Lists.newArrayList(this.getCloudList())).where(s -> s.enabled()).findOne().isPresent();
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

    private List<CloudStorageInterface> getCloudList() {
        return SpringUtil.getBeansOfType(CloudStorageInterface.class)
                .values()
                .stream().filter(s -> !(s instanceof CloudStorageImplement))
                .toList();
    }

    private CloudStorageInterface getCloud() {
        return JinqStream.from(this.getCloudList()).where((s) -> s.enabled()).getOnlyValue();
    }

}
