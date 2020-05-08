package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Created by Thanh on 3/24/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDetailResult extends OrderSimpleResult implements Parcelable {

    public static final Creator<OrderDetailResult> CREATOR = new Creator<OrderDetailResult>() {
        public OrderDetailResult createFromParcel(Parcel in) {
            return new OrderDetailResult(in);
        }

        public OrderDetailResult[] newArray(int size) {
            return new OrderDetailResult[size];
        }
    };

    @JsonProperty("details")
    private OrderDetailItem[] products;
    @JsonProperty("promotionResults")
    private OrderPromotion[] promotions;


    public OrderDetailResult() {
    }

    public OrderDetailResult(Parcel in) {
        super(in);
        products = in.createTypedArray(OrderDetailItem.CREATOR);
        promotions = in.createTypedArray(OrderPromotion.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag) {
        super.writeToParcel(dest, flag);
        dest.writeTypedArray(products, flag);
        dest.writeTypedArray(promotions, flag);
    }

    public OrderDetailItem[] getProducts() {
        return products;
    }

    public void setProducts(OrderDetailItem[] products) {
        this.products = products;
    }

    public OrderPromotion[] getPromotions() {
        return promotions;
    }

    public void setPromotions(OrderPromotion[] promotions) {
        this.promotions = promotions;
    }



    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderDetailItem implements Parcelable {

        public static final Creator<OrderDetailItem> CREATOR = new Creator<OrderDetailItem>() {
            public OrderDetailItem createFromParcel(Parcel in) {
                return new OrderDetailItem(in);
            }

            public OrderDetailItem[] newArray(int size) {
                return new OrderDetailItem[size];
            }
        };

        private OrderDetailProduct product;
        private BigDecimal quantity;

        public OrderDetailItem() {
        }

        public OrderDetailItem(Parcel in) {
            product = in.readParcelable(getClass().getClassLoader());
            String number = in.readString();
            quantity = number != null ? new BigDecimal(number) : null;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(product, flags);
            dest.writeString(quantity != null ? quantity.toString() : null);
        }

        public OrderDetailProduct getProduct() {
            return product;
        }

        public void setProduct(OrderDetailProduct product) {
            this.product = product;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderDetailProduct implements Parcelable {

        public static final Creator<OrderDetailProduct> CREATOR = new Creator<OrderDetailProduct>() {
            public OrderDetailProduct createFromParcel(Parcel in) {
                return new OrderDetailProduct(in);
            }

            public OrderDetailProduct[] newArray(int size) {
                return new OrderDetailProduct[size];
            }
        };

        private String id;
        private String name;
        private String code;
        private UOM uom;
        private String photo;
        private BigDecimal price;

        public OrderDetailProduct() {
        }

        public OrderDetailProduct(Parcel in) {
            id = in.readString();
            name = in.readString();
            code = in.readString();
            uom = in.readParcelable(getClass().getClassLoader());
            photo = in.readString();
            String number = in.readString();
            price = number == null ? null : new BigDecimal(number);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
            dest.writeString(code);
            dest.writeParcelable(uom, flags);
            dest.writeString(photo);
            dest.writeString(price == null ? null : price.toString());
        }

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

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
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

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }
    }

}
