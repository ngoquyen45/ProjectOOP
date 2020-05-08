package com.viettel.backend.engine.file;

import java.io.InputStream;
import java.util.Collection;

public interface FileEngine {
    
    // IMAGES
    public DbFile getImage(String fileId);
    
    public String storeImage(InputStream inputStream, String originalFileName, String contentType, DbFileMeta metaData,
            String sizeType);

    // FILES
    public String store(Object context, InputStream inputStream, String originalFileName, String contentType,
            DbFileMeta metaData);

    public DbFile get(Object context, String fileId);

    public void delete(Object context, String fileId);

    public void delete(Object context, Collection<String> fileIds);

    public void markAsUsed(Object context, String fileId);

    public void markAsUsed(Object context, Collection<String> fileIds);

    public boolean exists(Object context, String fileId);

    public boolean exists(Object context, Collection<String> fileIds);

}
