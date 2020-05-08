package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Thanh on 3/20/2015.
 */
public class OrderHolder implements Parcelable, Serializable {

    public static final Creator<OrderHolder> CREATOR = new Creator<OrderHolder>() {
        public OrderHolder createFromParcel(Parcel in) {
            return new OrderHolder(in);
        }

        public OrderHolder[] newArray(int size) {
            return new OrderHolder[size];
        }
    };

    public PlaceOrderProduct[] productsSelected;
    public OrderPromotion[] orderPromotions;
    public int deliveryType;
    public int[] deliveryDay;
    public int[] deliveryTime;
    public BigDecimal discountAmount;
    public BigDecimal totalAmount;
    public boolean orderJoinDP;

    public OrderHolder() {

    }

    public OrderHolder(Parcel in) {
        productsSelected = in.createTypedArray(PlaceOrderProduct.CREATOR);
        orderPromotions = in.createTypedArray(OrderPromotion.CREATOR);
        deliveryType = in.readInt();
        deliveryDay = in.createIntArray();
        deliveryTime = in.createIntArray();
        String number = in.readString();
        discountAmount = number == null ? null : new BigDecimal(number);
        number = in.readString();
        totalAmount = number == null ? null : new BigDecimal(number);
        orderJoinDP = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(productsSelected, flags);
        dest.writeTypedArray(orderPromotions, flags);
        dest.writeInt(deliveryType);
        dest.writeIntArray(deliveryDay);
        dest.writeIntArray(deliveryTime);
        dest.writeString(discountAmount == null ? null : discountAmount.toString());
        dest.writeString(totalAmount == null ? null : totalAmount.toString());
        dest.writeByte((byte) (orderJoinDP ? 1 : 0));
    }

    public PlaceOrderProduct[] getProductsSelected() {
        return productsSelected;
    }

    public void setProductsSelected(PlaceOrderProduct[] productsSelected) {
        this.productsSelected = productsSelected;
    }

    public OrderPromotion[] getOrderPromotions() {
        return orderPromotions;
    }

    public void setOrderPromotions(OrderPromotion[] orderPromotions) {
        this.orderPromotions = orderPromotions;
    }

    public int getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(int deliveryType) {
        this.deliveryType = deliveryType;
    }

    public int[] getDeliveryDay() {
        return deliveryDay;
    }

    public void setDeliveryDay(int[] deliveryDay) {
        this.deliveryDay = deliveryDay;
    }

    public int[] getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(int[] deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isOrderJoinDP() {
        return orderJoinDP;
    }

    public void setOrderJoinDP(boolean orderJoinDP) {
        this.orderJoinDP = orderJoinDP;
    }
}
