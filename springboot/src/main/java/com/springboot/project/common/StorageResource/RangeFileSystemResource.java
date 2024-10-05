package com.springboot.project.common.StorageResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.io.input.RandomAccessFileInputStream;
import org.springframework.core.io.FileSystemResource;

public class RangeFileSystemResource extends FileSystemResource {

    private long rangeContentLength;
    private File file;
    private long startIndex;

    public RangeFileSystemResource(File file, long startIndex, long rangeContentLength) {
        super(file);
        this.rangeContentLength = rangeContentLength;
        this.file = file;
        this.startIndex = startIndex;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream input = RandomAccessFileInputStream.builder()
                .setRandomAccessFile(new RandomAccessFile(this.file, "r"))
                .get();
        try {
            input.skip(startIndex);
            return BoundedInputStream.builder().setInputStream(input).setMaxCount(this.rangeContentLength).get();
        } catch (Throwable e) {
            input.close();
            throw e;
        }
    }

    @Override
    public long contentLength() throws IOException {
        return this.rangeContentLength;
    }

}
