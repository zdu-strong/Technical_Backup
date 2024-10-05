package com.springboot.project.common.StorageResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import org.apache.commons.io.input.RandomAccessFileInputStream;
import org.springframework.core.io.FileSystemResource;

public class RandomAccessFileSystemResource extends FileSystemResource {

    private File file;

    public RandomAccessFileSystemResource(File file) {
        super(file);
        this.file = file;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return RandomAccessFileInputStream.builder()
                .setRandomAccessFile(new RandomAccessFile(this.file, "r"))
                .get();
    }

}
