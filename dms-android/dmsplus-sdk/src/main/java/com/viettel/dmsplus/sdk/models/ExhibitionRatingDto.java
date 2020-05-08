package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by PHAMHUNG on 5/21/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public  class ExhibitionRatingDto implements Parcelable, Serializable {
    String exhibitionId; // Id chuong trinh trung bay
    ExhibitionRatingItemDto[] items; // Danh sach cac criteria duoc rate

    public String getExhibitionId() {
        return exhibitionId;
    }

    public void setExhibitionId(String exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    public ExhibitionRatingItemDto[] getItems() {
        return items;
    }

    public void setItems(ExhibitionRatingItemDto[] items) {
        this.items = items;
    }

    public static final Creator<ExhibitionRatingDto> CREATOR = new Creator<ExhibitionRatingDto>() {
        @Override
        public ExhibitionRatingDto createFromParcel(Parcel parcel) {
            return new ExhibitionRatingDto(parcel);
        }

        @Override
        public ExhibitionRatingDto[] newArray(int i) {
            return new ExhibitionRatingDto[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(exhibitionId);
        parcel.writeTypedArray(items, i);
    }

    public ExhibitionRatingDto() {
    }

    public ExhibitionRatingDto(Parcel in) {
        exhibitionId = in.readString();
        items = in.createTypedArray(ExhibitionRatingItemDto.CREATOR);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
   public static class ExhibitionRatingItemDto implements Parcelable,Serializable {
        private String exhibitionItemId;
        private float rate;
        public ExhibitionRatingItemDto(){}
        public String getExhibitionItemId() {
            return exhibitionItemId;
        }

        public void setExhibitionItemId(String exhibitionItemId) {
            this.exhibitionItemId = exhibitionItemId;
        }

        public float getRate() {
            return rate;
        }

        public void setRate(float rate) {
            this.rate = rate;
        }

        public static final Creator<ExhibitionRatingItemDto> CREATOR = new Creator<ExhibitionRatingItemDto>() {

            @Override
            public ExhibitionRatingItemDto createFromParcel(Parcel parcel) {
                return new ExhibitionRatingItemDto(parcel);
            }

            @Override
            public ExhibitionRatingItemDto[] newArray(int i) {
                return new ExhibitionRatingItemDto[i];
            }
        };
        public ExhibitionRatingItemDto(Parcel in){
            exhibitionItemId = in.readString();
            rate = in.readFloat();
        }
        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(exhibitionItemId);
            parcel.writeFloat(rate);
        }
    }
}


