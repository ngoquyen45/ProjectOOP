package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.viettel.dmsplus.sdk.network.IsoDateDeserializer;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by PHAMHUNG on 2/2/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeReturnSimpleDto extends IdDto implements Parcelable {

    CategorySimple distributor;
    CustomerSimple customer;
    UserSimple createdBy;
    @JsonDeserialize(using = IsoDateDeserializer.class)
    private Date createdTime;
    BigDecimal quantity;

    public static final Creator<ExchangeReturnSimpleDto> CREATOR = new Creator<ExchangeReturnSimpleDto>() {
        public ExchangeReturnSimpleDto createFromParcel(Parcel source) {
            return new ExchangeReturnSimpleDto(source);
        }

        public ExchangeReturnSimpleDto[] newArray(int size) {
            return new ExchangeReturnSimpleDto[size];
        }
    };

    public ExchangeReturnSimpleDto() {
    }

    public ExchangeReturnSimpleDto(Parcel in) {
        this.distributor = in.readParcelable(CategorySimple.class.getClassLoader());
        this.customer = in.readParcelable(CustomerSimple.class.getClassLoader());
        this.createdBy = in.readParcelable(UserSimple.class.getClassLoader());
        long tmpCreatedTime = in.readLong();
        this.createdTime = tmpCreatedTime == -1 ? null : new Date(tmpCreatedTime);
        String number = in.readString();
        quantity = number == null ? null : new BigDecimal(number);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.distributor, flags);
        dest.writeParcelable(this.customer, flags);
        dest.writeParcelable(this.createdBy, flags);
        dest.writeLong(createdTime != null ? createdTime.getTime() : -1);
        dest.writeString(quantity == null ? null : quantity.toString());
    }

    public CategorySimple getDistributor() {
        return distributor;
    }

    public void setDistributor(CategorySimple distributor) {
        this.distributor = distributor;
    }

    public CustomerSimple getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerSimple customer) {
        this.customer = customer;
    }

    public UserSimple getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserSimple createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
