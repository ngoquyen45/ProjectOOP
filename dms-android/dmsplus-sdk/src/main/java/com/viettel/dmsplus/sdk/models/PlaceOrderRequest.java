package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.dmsplus.sdk.network.IsoDateSerializer;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Thanh on 3/11/2015.
 */
public class PlaceOrderRequest implements Parcelable {

    public static final Creator<PlaceOrderRequest> CREATOR = new Creator<PlaceOrderRequest>() {
        public PlaceOrderRequest createFromParcel(Parcel in) {
            return new PlaceOrderRequest(in);
        }

        public PlaceOrderRequest[] newArray(int size) {
            return new PlaceOrderRequest[size];
        }
    };

    private ProductAndQuantity[] details;
    private BigDecimal discountAmt;
    private BigDecimal discount;
    private int deliveryType;
    @JsonSerialize(using = IsoDateSerializer.class)
    private Date deliveryTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String customerId;

    private boolean orderForDP;
    private boolean vanSales;

    public PlaceOrderRequest() {

    }

    public PlaceOrderRequest(Parcel in) {
        details = in.createTypedArray(ProductAndQuantity.CREATOR);
        String number = in.readString();
        discountAmt = number == null ? null : new BigDecimal(number);
        number = in.readString();
        discount = number == null ? null : new BigDecimal(number);
        deliveryType = in.readInt();
        long date = in.readLong();
        setDeliveryTime(date == 0 ? null : new Date(date));
        customerId = in.readString();
        orderForDP = in.readByte() != 0;
        vanSales = in.readByte()!=0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(details, flags);
        dest.writeString(discountAmt == null ? null : discountAmt.toString());
        dest.writeString(discount == null ? null : discount.toString());
        dest.writeInt(deliveryType);
        dest.writeLong(getDeliveryTime() == null ? 0 : getDeliveryTime().getTime());
        dest.writeString(customerId);
        dest.writeByte((byte) (orderForDP ? 1 : 0));
        dest.writeByte((byte) (vanSales ? 1 : 0));
    }

    public ProductAndQuantity[] getDetails() {
        return details;
    }

    public void setDetails(ProductAndQuantity[] details) {
        this.details = details;
    }

    public BigDecimal getDiscountAmt() {
        return discountAmt;
    }

    public void setDiscountAmt(BigDecimal discountAmt) {
        this.discountAmt = discountAmt;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public int getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(int deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public boolean isOrderForDP() {
        return orderForDP;
    }

    public void setOrderForDP(boolean orderForDP) {
        this.orderForDP = orderForDP;
    }

    public boolean isVanSales() {
        return vanSales;
    }

    public void setVanSales(boolean vanSales) {
        this.vanSales = vanSales;
    }

    public static class PlaceOrderRequestPromotionItem implements Parcelable {

        public static final Creator<PlaceOrderRequestPromotionItem> CREATOR = new Creator<PlaceOrderRequestPromotionItem>() {
            public PlaceOrderRequestPromotionItem createFromParcel(Parcel in) {
                return new PlaceOrderRequestPromotionItem(in);
            }

            public PlaceOrderRequestPromotionItem[] newArray(int size) {
                return new PlaceOrderRequestPromotionItem[size];
            }
        };

        @JsonProperty("id")
        private String promotionProgramId;

        private PlaceOrderRequestPromotionItemDetail[] details;

        public PlaceOrderRequestPromotionItem() {

        }

        public PlaceOrderRequestPromotionItem(Parcel in) {
            promotionProgramId = in.readString();
            details = in.createTypedArray(PlaceOrderRequestPromotionItemDetail.CREATOR);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(promotionProgramId);
            dest.writeParcelableArray(details, flags);
        }

        public String getPromotionProgramId() {
            return promotionProgramId;
        }

        public void setPromotionProgramId(String promotionProgramId) {
            this.promotionProgramId = promotionProgramId;
        }

        public PlaceOrderRequestPromotionItemDetail[] getDetails() {
            return details;
        }

        public void setDetails(PlaceOrderRequestPromotionItemDetail[] details) {
            this.details = details;
        }
    }

    public static class PlaceOrderRequestPromotionItemDetail implements Parcelable {

        public static final Creator<PlaceOrderRequestPromotionItemDetail> CREATOR = new Creator<PlaceOrderRequestPromotionItemDetail>() {
            public PlaceOrderRequestPromotionItemDetail createFromParcel(Parcel in) {
                return new PlaceOrderRequestPromotionItemDetail(in);
            }

            public PlaceOrderRequestPromotionItemDetail[] newArray(int size) {
                return new PlaceOrderRequestPromotionItemDetail[size];
            }
        };

        @JsonProperty("promotionDetailId")
        private String promotionId;

        @JsonProperty("groupId")
        private String rewardId;

        public PlaceOrderRequestPromotionItemDetail() {

        }

        public PlaceOrderRequestPromotionItemDetail(Parcel in) {
            promotionId = in.readString();
            rewardId = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(promotionId);
            dest.writeString(rewardId);
        }

        public String getPromotionId() {
            return promotionId;
        }

        public void setPromotionId(String promotionId) {
            this.promotionId = promotionId;
        }

        public String getRewardId() {
            return rewardId;
        }

        public void setRewardId(String rewardId) {
            this.rewardId = rewardId;
        }
    }
}
