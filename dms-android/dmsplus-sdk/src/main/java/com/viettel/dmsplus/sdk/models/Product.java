package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * @author PHAMHUNG
 * @since 14/4/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product extends CategorySimple implements Parcelable {

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    private UOM uom;
    private BigDecimal price;
    private String description;
    private String photo;

    public Product() {

    }

    public Product(Parcel in) {
        super(in);

        ClassLoader classLoader = getClass().getClassLoader();
        uom = in.readParcelable(classLoader);
        String number = in.readString();
        price = TextUtils.isEmpty(number) ? null : new BigDecimal(number);
        description = in.readString();
        photo = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeParcelable(uom, flags);
        dest.writeString(price == null ? "" : price.toString());
        dest.writeString(description);
        dest.writeString(photo);
    }

    public UOM getUom() {
        return uom;
    }

    public void setUom(UOM uom) {
        this.uom = uom;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
