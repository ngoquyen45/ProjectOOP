package com.viettel.backend.domain.embed;

import java.io.Serializable;

import org.bson.types.ObjectId;

/**
 * @author thanh
 */
public class ExhibitionRatingItem implements Serializable {

    private static final long serialVersionUID = 3227127774937212395L;

    public static final String COLUMNNAME_EXHIBITION_ITEM_ID = "exhibitionItemId";
    public static final String COLUMNNAME_RATE = "rate";

    private ObjectId exhibitionItemId;
    private float rate;

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public ObjectId getExhibitionItemId() {
        return exhibitionItemId;
    }

    public void setExhibitionItemId(ObjectId exhibitionItemId) {
        this.exhibitionItemId = exhibitionItemId;
    }

}
