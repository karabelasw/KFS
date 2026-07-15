package com.karabelas.kfs.config;

// import org.springframework.boot.context.properties.ConfigurationProperties;
// import org.springframework.util.unit.DataSize;
// import java.nio.file.Path;

/**
 * Mock: @ConfigurationProperties(prefix = "kfs.storage")
 * Backs kfs.storage.root-path and kfs.storage.max-file-size discussed
 * in prior sessions. Real class will use Path / DataSize types.
 */
public class StorageProperties {
    private String rootPath;
    private long maxFileSize;
}
