package com.springboot.project.common.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.springboot.project.model.StorageFileModel;

@Component
public class BaseStorageSave extends BaseStorageCreateTempFile {
	public StorageFileModel storageResource(Resource resource) {
		try {
			var storageFileModel = new StorageFileModel().setFolderName(UUID.randomUUID().toString());

			/* 设置文件名称 */
			storageFileModel.setFileName(this.getFileNameFromResource(resource));

			/* 设置文件夹大小 */
			if (resource.isFile()) {
				File sourceFile = resource.getFile();
				if (!sourceFile.exists()) {
					throw new RuntimeException("Resource does not exist");
				}
				if (sourceFile.isDirectory()) {
					storageFileModel.setFolderSize(FileUtils.sizeOfDirectory(sourceFile));
				} else {
					storageFileModel.setFolderSize(resource.contentLength());
				}
			} else {
				storageFileModel.setFolderSize(resource.contentLength());
			}

			/* 获取相对路径 */
			String relativePath = this.getRelativePathFromResourcePath(storageFileModel.getFolderName());
			if (storageFileModel.getFileName() != null) {
				relativePath = this.getRelativePathFromResourcePath(
						Paths.get(storageFileModel.getFolderName(), storageFileModel.getFileName()).toString());
			}

			/* 设置相对路径 */
			storageFileModel.setRelativePath(relativePath);

			/* 设置相对url */
			storageFileModel.setRelativeUrl(this.getResoureUrlFromResourcePath(relativePath));

			/* 设置相对下载url */
			storageFileModel.setRelativeDownloadUrl("/download" + storageFileModel.getRelativeUrl());

			if (resource.isFile()) {
				File sourceFile = resource.getFile();
				if (!sourceFile.exists()) {
					throw new RuntimeException("Resource does not exist");
				}
				try {
					if (sourceFile.isDirectory()) {
						FileUtils.copyDirectory(sourceFile, new File(this.getRootPath(), relativePath));
					} else {
						FileUtils.copyFile(sourceFile, new File(this.getRootPath(), relativePath));
					}
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			} else {
				try (InputStream input = resource.getInputStream()) {
					FileUtils.copyInputStreamToFile(input, new File(this.getRootPath(), relativePath));
				}
			}
			return storageFileModel;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public StorageFileModel storageResource(MultipartFile file) {
		var tempFile = this.createTempFile(file);
		var storageFileModel = this.storageResource(new FileSystemResource(tempFile));
		this.delete(tempFile);
		return storageFileModel;
	}

	public StorageFileModel storageResourceAsFolderFromZipResource(MultipartFile file) {
		var tempFolder = this.createTempFolderByDecompressingZipResource(file);
		var storageFileModel = this.storageResource(new FileSystemResource(tempFolder));
		this.delete(tempFolder);
		return storageFileModel;

	}

}
