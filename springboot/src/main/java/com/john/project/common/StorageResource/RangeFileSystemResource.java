package com.john.project.common.StorageResource;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.io.input.RandomAccessFileInputStream;
import org.springframework.core.io.FileSystemResource;
import lombok.SneakyThrows;

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
    @SneakyThrows
    public InputStream getInputStream() {
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
    public long contentLength() {
        return this.rangeContentLength;
    }

}
