package com.springboot.project.common.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.io.input.RandomAccessFileInputStream;
import org.springframework.core.io.FileSystemResource;

public class RangeFileSystemResource extends FileSystemResource {

	private long contentLength;
	private File file;
	private long start;

	public RangeFileSystemResource(File file, long start, long length) {
		super(file);
		this.contentLength = length;
		this.file = file;
		this.start = start;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		InputStream input = null;
		try {
			input = new RandomAccessFileInputStream(new RandomAccessFile(this.file, "r"));
			input.skip(start);
			return new BoundedInputStream(input, this.contentLength);
		} catch (Throwable e) {
			if (input != null) {
				input.close();
			}
			throw e;
		}
	}

	@Override
	public long contentLength() throws IOException {
		return this.contentLength;
	}

}
