package com.viettel.backend.repository;

import java.io.InputStream;
import java.util.Collection;

import org.bson.types.ObjectId;

import com.viettel.backend.engine.file.DbFile;
import com.viettel.backend.engine.file.DbFileMeta;

public interface FileRepository {
    
    // IMAGES
    public DbFile getImageById(ObjectId fileId);
    
    public String storeImage(InputStream inputStream, String fileName, String contentType, DbFileMeta metaData);

    // FILES
    public DbFile getFileById(ObjectId clientId, ObjectId fileId);

    public Collection<DbFile> getFileByIds(ObjectId clientId, Collection<ObjectId> fileIds);

    public String store(ObjectId clientId, InputStream inputStream, String fileName, String contentType,
            DbFileMeta metaData);

    public void delete(ObjectId clientId, ObjectId fileId);

    public void delete(ObjectId clientId, Collection<ObjectId> fileIds);

    public void markAsUsed(ObjectId clientId, ObjectId fileId);

    public void markAsUsed(ObjectId clientId, Collection<ObjectId> fileIds);

    public boolean exists(ObjectId clientId, ObjectId fileId);

    public boolean existsAll(ObjectId clientId, Collection<ObjectId> fileIds);

}
