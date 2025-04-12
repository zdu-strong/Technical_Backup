package com.john.project.common.storage;

import java.io.File;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class BaseStorageDeleteResource extends BaseStorage {

    public void delete(HttpServletRequest request) {
        String relativePath = this.getRelativePathFromRequest(request);
        this.delete(new File(this.getRootPath(), relativePath));
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported path");
        }
        if (path.equals(this.getRootPath())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported path");
        }
        return this.getRelativePathFromResourcePath(
                Paths.get(this.getRootPath()).relativize(Paths.get(path)).normalize().toString());
    }

}
