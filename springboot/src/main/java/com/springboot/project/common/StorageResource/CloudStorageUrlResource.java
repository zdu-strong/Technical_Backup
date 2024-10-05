package com.springboot.project.common.StorageResource;

import java.io.IOException;
import java.net.URL;
import org.springframework.core.io.UrlResource;

public class CloudStorageUrlResource extends UrlResource {

	private long contentLength;

	public CloudStorageUrlResource(URL url, long contentLength) {
		super(url);
		this.contentLength = contentLength;
	}

	@Override
	public long contentLength() throws IOException {
		return this.contentLength;
	}

}
