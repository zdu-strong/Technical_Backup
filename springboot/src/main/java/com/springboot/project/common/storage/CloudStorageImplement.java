package com.springboot.project.common.storage;

import java.io.File;
import org.jinq.orm.stream.JinqStream;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.google.common.collect.Lists;
import io.reactivex.Observable;

@Component
public class CloudStorageImplement implements CloudStorageInterface {

	@Override
	public boolean enabled() {
		return JinqStream.from(Lists.newArrayList(this.getCloudList())).where(s -> s.enabled()).findOne().isPresent();
	}

	@Override
	public File createTempFileOrFolder(String key) {
		return this.getCloud().createTempFileOrFolder(key);
	}

	@Override
	public void storageResource(File sourceFileOrFolder, String key) {
		this.getCloud().storageResource(sourceFileOrFolder, key);
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

	@Override
	public Observable<String> getRootList() {
		return this.getCloud().getRootList();
	}

	private CloudStorageInterface[] getCloudList() {
		return new CloudStorageInterface[] {};
	}

	private CloudStorageInterface getCloud() {
		return JinqStream.from(Lists.newArrayList(this.getCloudList())).where((s) -> s.enabled()).getOnlyValue();
	}

}
