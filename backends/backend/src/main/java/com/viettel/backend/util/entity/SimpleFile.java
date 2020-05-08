package com.viettel.backend.util.entity;

import java.io.Serializable;

public class SimpleFile implements Serializable {

    private static final long serialVersionUID = -3863564147141059620L;
    
    private String fileId;
    private String name;

    public String getFileId() {
        return fileId;
    }
    
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
