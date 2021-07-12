package com.springboot.project.common.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.google.common.base.Strings;
import cn.hutool.core.util.ZipUtil;

@Component
public class BaseStorageCreateTempFile extends BaseStorageGetResourceForRequest {

	/**
	 * Create temporary files or folders based on the relative path of the resource
	 * 
	 * @param relativePathOfResource
	 * @return
	 */
	public File createTempFileOrFolder(String relativePathOfResource) {
		var request = new MockHttpServletRequest();
		request.setRequestURI(this.getResoureUrlFromResourcePath(relativePathOfResource));
		var resource = this.getResourceFromRequest(request);
		return this.createTempFileOrFolder(resource);
	}

	public File createTempFileOrFolder(Resource resource) {
		try {
			if (resource.isFile()) {
				var resourceFile = resource.getFile();
				if (resourceFile.isDirectory()) {
					File targetFolder = this.createTempFolder();
					FileUtils.copyDirectory(resourceFile, targetFolder);
					return targetFolder;
				} else {
					File targetFile = new File(this.createTempFolder(), resource.getFilename());
					try (InputStream input = resource.getInputStream()) {
						FileUtils.copyInputStreamToFile(input, targetFile);
						return targetFile;
					}
				}
			} else {
				String fileName = this.getFileNameFromResource(resource);
				if (Strings.isNullOrEmpty(fileName)) {
					throw new RuntimeException("File name cannot be empty");
				}
				File targetFile = new File(this.createTempFolder(), fileName);
				try (InputStream input = resource.getInputStream()) {
					FileUtils.copyInputStreamToFile(input, targetFile);
					return targetFile;
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public File createTempFile(MultipartFile file) {
		try {
			File targetFile = new File(this.createTempFolder(), file.getOriginalFilename());
			try (InputStream input = file.getInputStream()) {
				FileUtils.copyInputStreamToFile(input, targetFile);
				return targetFile;
			}
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public File createTempFolder() {
		File tempFolder = new File(this.getRootPath(), UUID.randomUUID().toString());
		tempFolder.mkdirs();
		return tempFolder;
	}

	public File createTempFolderByDecompressingZipResource(Resource resourceOfZipFile) {
		try (var input = new ZipInputStream(resourceOfZipFile.getInputStream())) {
			File tempFolder = this.createTempFolder();
			ZipUtil.unzip(input, tempFolder);
			return tempFolder;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public File createTempFolderByDecompressingZipResource(MultipartFile resourceOfZipFile) {
		try (var input = new ZipInputStream(resourceOfZipFile.getInputStream())) {
			File tempFolder = this.createTempFolder();
			ZipUtil.unzip(input, tempFolder);
			return tempFolder;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
