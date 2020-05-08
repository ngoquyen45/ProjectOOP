package com.viettel.backend.dto.imp;

import java.io.Serializable;

public class ImportProductPhotoDto implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String[] photos;

    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
    }

}
