package com.springboot.project.common.storage;

import java.io.File;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import io.reactivex.Observable;

@Component
public interface CloudStorageInterface {
	boolean enabled();

	File createTempFileOrFolder(String key);

	void storageResource(File sourceFileOrFolder, String key);

	void delete(String key);

	Resource getResource(String key);

	Resource getResource(String key, long startIndex, long rangeContentLength);

	Observable<String> getRootList();
}