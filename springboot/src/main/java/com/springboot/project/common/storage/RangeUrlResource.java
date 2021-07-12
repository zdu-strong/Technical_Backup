package com.springboot.project.common.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.io.input.BoundedInputStream;
import org.springframework.core.io.UrlResource;

public class RangeUrlResource extends UrlResource {
	private long start;
	private long contentLength;

	public RangeUrlResource(URL url, long start, long length) {
		super(url);
		this.start = start;
		this.contentLength = length;
	}

	@Override
	public long contentLength() {
		return this.contentLength;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		InputStream input = null;
		try {
			input = super.getInputStream();
			input.skip(this.start);
			return new BoundedInputStream(input, this.contentLength);
		} catch (Throwable e) {
			if (input != null) {
				input.close();
			}
			throw e;
		}
	}

}
