package com.springboot.project.common.StorageResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.springframework.core.io.UrlResource;
import org.springframework.util.ResourceUtils;

import lombok.SneakyThrows;

public class RangeCloudStorageUrlResource extends UrlResource {
    private long startIndex;
    private long rangeContentLength;

    public RangeCloudStorageUrlResource(URL url, long startIndex, long rangeContentLength) {
        super(url);
        this.startIndex = startIndex;
        this.rangeContentLength = rangeContentLength;
    }

    @Override
    @SneakyThrows
    public InputStream getInputStream() {
        URLConnection con = this.getURL().openConnection();
        con.setRequestProperty("range", "bytes=" + startIndex + "-" + (startIndex + rangeContentLength - 1));
        ResourceUtils.useCachesIfNecessary(con);
        try {
            return con.getInputStream();
        } catch (IOException ex) {
            // Close the HTTP connection (if applicable).
            if (con instanceof HttpURLConnection) {
                ((HttpURLConnection) con).disconnect();
            }
            throw ex;
        }
    }

    @Override
    public long contentLength() {
        return this.rangeContentLength;
    }

}
