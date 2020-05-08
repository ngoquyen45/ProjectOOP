package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.viettel.dmsplus.sdk.network.IsoShortDateDeserializer;

import java.util.Date;

/**
 * @author Thanh
 * @since 3/10/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PromotionListItem extends CategorySimple {

    public static final Creator<PromotionListItem> CREATOR = new Creator<PromotionListItem>() {
        public PromotionListItem createFromParcel(Parcel in) {
            return new PromotionListItem(in);
        }

        public PromotionListItem[] newArray(int size) {
            return new PromotionListItem[size];
        }
    };

    @JsonDeserialize(using = IsoShortDateDeserializer.class)
    private Date startDate;
    @JsonDeserialize(using = IsoShortDateDeserializer.class)
    private Date endDate;
    private String applyFor;
    private String description;

    public PromotionListItem() {

    }

    public PromotionListItem(Parcel in) {
        super(in);

        long time = in.readLong();
        startDate = time == 0 ? null : new Date(time);
        time = in.readLong();
        endDate = time == 0 ? null : new Date(time);
        applyFor = in.readString();
        description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeLong(startDate == null ? 0 : startDate.getTime());
        dest.writeLong(endDate == null ? 0 : endDate.getTime());
        dest.writeString(applyFor);
        dest.writeString(description);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getApplyFor() {
        return applyFor;
    }

    public void setApplyFor(String applyFor) {
        this.applyFor = applyFor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
