package com.springboot.project.common.storage;

import java.io.File;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

@Component
public class BaseStorageDeleteResource extends BaseStorage {

    public void delete(String relativePathOfResource) {
        String relativePath = this.getRelativePathFromResourcePath(relativePathOfResource);
        FileUtils.deleteQuietly(new File(this.getRootPath(), relativePath));
        if (new File(this.getRootPath(), relativePath).exists()) {
            throw new RuntimeException("Failed to delete files. File path is " + relativePath);
        }
        if (this.cloud.enabled()) {
            this.cloud.delete(relativePath);
        }
    }

    public void delete(File fileOrFolder) {
        String relativePath = this.getRelativePathFromFileOrFolder(fileOrFolder);
        FileUtils.deleteQuietly(new File(this.getRootPath(), relativePath));
    }

    private String getRelativePathFromFileOrFolder(File fileOrFolder) {
        String path = Paths.get(fileOrFolder.getAbsolutePath()).normalize().toString().replaceAll(Pattern.quote("\\"),
                "/");
        if (!path.startsWith(this.getRootPath())) {
            throw new RuntimeException("Unsupported path");
        }
        if (path.equals(this.getRootPath())) {
            throw new RuntimeException("Unsupported path");
        }
        return this.getRelativePathFromResourcePath(
                Paths.get(this.getRootPath()).relativize(Paths.get(path)).normalize().toString());
    }

}
