package com.viettel.backend.util.entity;

import java.io.InputStream;
import java.io.Serializable;

public class ExportFile implements Serializable {

    private static final long serialVersionUID = 3147185998558357275L;
    
    private String fileName;
    private InputStream inputStream;
    
    public ExportFile(String fileName, InputStream inputStream) {
        super();
        this.fileName = fileName;
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

}
