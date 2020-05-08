package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.viettel.dmsplus.sdk.network.IsoShortDateDeserializer;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by PHAMHUNG on 4/15/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExhibitionDto implements Parcelable {
    @JsonDeserialize(using = IsoShortDateDeserializer.class)
    Date startDate;
    @JsonDeserialize(using = IsoShortDateDeserializer.class)
    Date endDate;
    /**
     * Only exist in client
     */
    String id;
    String name;
    String description;
    ExhibitionCategoryDto[] categories;
    boolean rated;

    public ExhibitionCategoryDto[] getCategories() {
        return categories;
    }

    public void setCategories(ExhibitionCategoryDto[] categories) {
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isRated() {
        return rated;
    }

    public void setRated(boolean rated) {
        this.rated = rated;
    }

    public static final Creator<ExhibitionDto> CREATOR = new Creator<ExhibitionDto>() {
        public ExhibitionDto createFromParcel(Parcel in) {
            return new ExhibitionDto(in);
        }

        public ExhibitionDto[] newArray(int size) {
            return new ExhibitionDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeTypedArray(categories, i);
        parcel.writeLong(startDate != null ? startDate.getTime() : 0);
        parcel.writeLong(endDate != null ? endDate.getTime() : 0);
        parcel.writeByte((byte) (rated ? 1 : 0));
    }

    ExhibitionDto() {
    }

    ExhibitionDto(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        categories = in.createTypedArray(ExhibitionCategoryDto.CREATOR);
        long time = in.readLong();
        startDate = time != 0 ? new Date(time) : null;
        time = in.readLong();
        endDate = time != 0 ? new Date(time) : null;
        rated = in.readByte()!=0;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExhibitionCategoryDto implements Parcelable {
        String id;
        String name;
        ExhibitionItemDto[] items;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ExhibitionItemDto[] getItems() {
            return items;
        }

        public void setItems(ExhibitionItemDto[] items) {
            this.items = items;
        }

        public static final Creator<ExhibitionCategoryDto> CREATOR = new Creator<ExhibitionCategoryDto>() {
            @Override
            public ExhibitionCategoryDto createFromParcel(Parcel parcel) {
                return new ExhibitionCategoryDto(parcel);
            }

            @Override
            public ExhibitionCategoryDto[] newArray(int i) {
                return new ExhibitionCategoryDto[i];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(id);
            parcel.writeString(name);
            parcel.writeTypedArray(items, i);
        }

        public ExhibitionCategoryDto() {
        }

        public ExhibitionCategoryDto(Parcel in) {
            id = in.readString();
            name = in.readString();
            items = in.createTypedArray(ExhibitionItemDto.CREATOR);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExhibitionItemDto implements Parcelable, Serializable {
        String id;
        String name;
        float rate;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public float getRate() {
            return rate;
        }

        public void setRate(float rate) {
            this.rate = rate;
        }

        public static final Creator<ExhibitionItemDto> CREATOR = new Creator<ExhibitionItemDto>() {

            @Override
            public ExhibitionItemDto createFromParcel(Parcel parcel) {
                return new ExhibitionItemDto(parcel);
            }

            @Override
            public ExhibitionItemDto[] newArray(int i) {
                return new ExhibitionItemDto[i];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(id);
            parcel.writeString(name);
            parcel.writeFloat(rate);
        }

        public ExhibitionItemDto() {
        }

        public ExhibitionItemDto(Parcel in) {
            id = in.readString();
            name = in.readString();
            rate = in.readFloat();
        }
    }


}