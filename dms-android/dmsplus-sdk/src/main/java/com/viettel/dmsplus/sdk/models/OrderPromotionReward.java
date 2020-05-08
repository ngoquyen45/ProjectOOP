package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Thanh
 * @since 3/9/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderPromotionReward implements Parcelable, Serializable {

    public static final Creator<OrderPromotionReward> CREATOR = new Creator<OrderPromotionReward>() {
        public OrderPromotionReward createFromParcel(Parcel in) {
            return new OrderPromotionReward(in);
        }

        public OrderPromotionReward[] newArray(int size) {
            return new OrderPromotionReward[size];
        }
    };

    private BigDecimal amount;
    private BigDecimal quantity;
    private Product product;

    public OrderPromotionReward() {

    }

    public OrderPromotionReward(Parcel in) {
        ClassLoader classLoader = getClass().getClassLoader();
        String number = in.readString();
        amount = TextUtils.isEmpty(number) ? null : new BigDecimal(number);
        number = in.readString();
        quantity = TextUtils.isEmpty(number) ? null : new BigDecimal(number);
        product = in.readParcelable(this.getClass().getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(amount == null ? "" : amount.toString());
        dest.writeString(quantity == null ? "" : quantity.toString());
        dest.writeParcelable(product, flags);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
