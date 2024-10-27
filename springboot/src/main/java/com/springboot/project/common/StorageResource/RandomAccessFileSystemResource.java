package com.springboot.project.common.StorageResource;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import org.apache.commons.io.input.RandomAccessFileInputStream;
import org.springframework.core.io.FileSystemResource;
import lombok.SneakyThrows;

public class RandomAccessFileSystemResource extends FileSystemResource {

    private File file;

    public RandomAccessFileSystemResource(File file) {
        super(file);
        this.file = file;
    }

    @Override
    @SneakyThrows
    public InputStream getInputStream() {
        return RandomAccessFileInputStream.builder()
                .setRandomAccessFile(new RandomAccessFile(this.file, "r"))
                .get();
    }

}
