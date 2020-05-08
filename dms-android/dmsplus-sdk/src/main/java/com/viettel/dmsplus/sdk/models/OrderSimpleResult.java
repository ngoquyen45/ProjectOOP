package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.viettel.dmsplus.sdk.network.IsoDateDeserializer;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author thanh
 * @since 5/21/15
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderSimpleResult implements Parcelable, Comparable<OrderSimpleResult> {

    public static final Creator<OrderSimpleResult> CREATOR = new Creator<OrderSimpleResult>() {
        public OrderSimpleResult createFromParcel(Parcel source) {
            return new OrderSimpleResult(source);
        }

        public OrderSimpleResult[] newArray(int size) {
            return new OrderSimpleResult[size];
        }
    };

    private String id;
    private String code;

    @JsonDeserialize(using = IsoDateDeserializer.class)
    private Date createdTime;
    private int approveStatus;

    private int deliveryType;
    private String deliveryTime;

    private String comment;

    private BigDecimal subTotal;
    private BigDecimal promotionAmt;

    private BigDecimal discountAmt;
    private BigDecimal discountPercentage;

    private BigDecimal grandTotal;
    private BigDecimal quantity;
    private BigDecimal productivity;

    private boolean isVisit;
    private boolean vanSales;

    private CategorySimple distributor;
    private CustomerSimple customer;

    public OrderSimpleResult() {

    }

    public OrderSimpleResult(OrderSimpleResult other) {
        this.id = other.id;
        this.code = other.code;
        this.createdTime = other.createdTime;
        this.approveStatus = other.approveStatus;
        this.deliveryType = other.deliveryType;
        this.deliveryTime = other.deliveryTime;
        this.comment = other.comment;
        this.subTotal = other.subTotal;
        this.promotionAmt = other.promotionAmt;
        this.discountAmt = other.discountAmt;
        this.discountPercentage = other.discountPercentage;
        this.grandTotal = other.grandTotal;
        this.quantity = other.quantity;
        this.productivity = other.productivity;
        this.isVisit = other.isVisit;
        this.vanSales = other.vanSales;
        this.distributor = other.distributor;
        this.customer = other.customer;
    }

    protected OrderSimpleResult(Parcel in) {
        this.id = in.readString();
        this.code = in.readString();
        long tmpCreatedTime = in.readLong();
        this.createdTime = tmpCreatedTime == -1 ? null : new Date(tmpCreatedTime);
        this.approveStatus = in.readInt();
        this.deliveryType = in.readInt();
        this.deliveryTime = in.readString();
        this.comment = in.readString();
        this.subTotal = (BigDecimal) in.readSerializable();
        this.promotionAmt = (BigDecimal) in.readSerializable();
        this.discountAmt = (BigDecimal) in.readSerializable();
        this.discountPercentage = (BigDecimal) in.readSerializable();
        this.grandTotal = (BigDecimal) in.readSerializable();
        this.quantity = (BigDecimal) in.readSerializable();
        this.productivity = (BigDecimal) in.readSerializable();
        this.isVisit = in.readByte() != 0;
        this.vanSales = in.readByte() != 0;
        this.distributor = in.readParcelable(CategorySimple.class.getClassLoader());
        this.customer = in.readParcelable(CustomerSimple.class.getClassLoader());
    }

    @Override
    public int compareTo(@NonNull OrderSimpleResult orderSimpleResult) {
        return this.createdTime.compareTo(orderSimpleResult.createdTime) * (-1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.code);
        dest.writeLong(createdTime != null ? createdTime.getTime() : -1);
        dest.writeInt(this.approveStatus);
        dest.writeInt(this.deliveryType);
        dest.writeString(this.deliveryTime);
        dest.writeString(this.comment);
        dest.writeSerializable(this.subTotal);
        dest.writeSerializable(this.promotionAmt);
        dest.writeSerializable(this.discountAmt);
        dest.writeSerializable(this.discountPercentage);
        dest.writeSerializable(this.grandTotal);
        dest.writeSerializable(this.quantity);
        dest.writeSerializable(this.productivity);
        dest.writeByte(isVisit ? (byte) 1 : (byte) 0);
        dest.writeByte(vanSales ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.distributor, 0);
        dest.writeParcelable(this.customer, 0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public int getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(int approveStatus) {
        this.approveStatus = approveStatus;
    }

    public int getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(int deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getPromotionAmt() {
        return promotionAmt;
    }

    public void setPromotionAmt(BigDecimal promotionAmt) {
        this.promotionAmt = promotionAmt;
    }

    public BigDecimal getDiscountAmt() {
        return discountAmt;
    }

    public void setDiscountAmt(BigDecimal discountAmt) {
        this.discountAmt = discountAmt;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getProductivity() {
        return productivity;
    }

    public void setProductivity(BigDecimal productivity) {
        this.productivity = productivity;
    }

    public boolean isVisit() {
        return isVisit;
    }

    public void setVisit(boolean isVisit) {
        this.isVisit = isVisit;
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
    public boolean isVanSales() {
        return vanSales;
    }

    public void setVanSales(boolean vanSales) {
        this.vanSales = vanSales;
    }
}
