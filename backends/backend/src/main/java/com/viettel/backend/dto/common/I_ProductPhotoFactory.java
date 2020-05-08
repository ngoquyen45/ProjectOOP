package com.viettel.backend.dto.common;

import org.bson.types.ObjectId;

public interface I_ProductPhotoFactory {

    /**
     * Get photo of product
     * 
     * @param productId
     * @return photo of product or default photo i case productId null, product
     *         not found, product don't have photo
     */
    public String getPhoto(ObjectId productId);

}
