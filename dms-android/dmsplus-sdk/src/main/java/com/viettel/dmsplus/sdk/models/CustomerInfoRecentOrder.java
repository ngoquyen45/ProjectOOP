package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.viettel.dmsplus.sdk.network.IsoDateDeserializer;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author PHAMHUNG
 * @since 3/2/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerInfoRecentOrder implements Parcelable {

    public static final Creator<CustomerInfoRecentOrder> CREATOR = new Creator<CustomerInfoRecentOrder>() {
        public CustomerInfoRecentOrder createFromParcel(Parcel in) {
            return new CustomerInfoRecentOrder(in);
        }

        public CustomerInfoRecentOrder[] newArray(int size) {
            return new CustomerInfoRecentOrder[size];
        }
    };

    @JsonDeserialize(using = IsoDateDeserializer.class)
    private Date date;

    @JsonProperty("code")
    private String order;

    @JsonProperty("skuNumber")
    private String sku;

    @JsonProperty("total")
    private BigDecimal price;

    public CustomerInfoRecentOrder() {

    }

    public CustomerInfoRecentOrder(Parcel in) {
        setDate(new Date(in.readLong()));
        setOrder(in.readString());
        setSku(in.readString());
        setPrice(new BigDecimal(in.readString()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getDate().getTime());
        dest.writeString(getOrder());
        dest.writeString(getSku());
        dest.writeString(getPrice().toString());
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
