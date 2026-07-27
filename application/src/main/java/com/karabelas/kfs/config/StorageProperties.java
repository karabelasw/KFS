package com.karabelas.kfs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Backs kfs.storage.root-path and kfs.storage.max-file-size.
 *
 * root-path is the absolute filesystem root that file.file_path
 * (relative paths only, per schema convention) is resolved against
 * at runtime. max-file-size is paired with Spring's own
 * spring.servlet.multipart.max-file-size at the transport layer —
 * this property is the application-level ceiling enforced in the
 * service layer, independent of that transport-level setting.
 */
@ConfigurationProperties(prefix = "kfs.storage")
public class StorageProperties {

    /** Absolute path on disk; file.file_path values are resolved relative to this. */
    private String rootPath;

    /** Max upload/attachment size in bytes. */
    private long maxFileSize;

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}
