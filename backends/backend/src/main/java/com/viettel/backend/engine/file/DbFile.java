package com.viettel.backend.engine.file;

import java.io.InputStream;
import java.io.Serializable;

public class DbFile implements Serializable {

    private static final long serialVersionUID = -7554122210866861409L;

    private String id;
    private String fileName;
    private DbFileMeta metaData;
    private InputStream inputStream;
    private String contentType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public DbFileMeta getMetaData() {
        return metaData;
    }

    public void setMetaData(DbFileMeta metaData) {
        this.metaData = metaData;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
