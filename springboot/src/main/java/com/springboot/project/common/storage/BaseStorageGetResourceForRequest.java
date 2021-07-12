package com.springboot.project.common.storage;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class BaseStorageGetResourceForRequest extends BaseStorageDeleteResource {
	public Resource getResourceFromRequest(HttpServletRequest request) {
		String relativePath = this.getRelativePathFromRequest(request);
		return new FileSystemResource(new File(this.getRootPath(), relativePath));
	}

	public Resource getResourceFromRequest(HttpServletRequest request, long start, long length) {
		String relativePath = this.getRelativePathFromRequest(request);
		return new RangeFileSystemResource(new File(this.getRootPath(), relativePath), start, length);
	}
}
