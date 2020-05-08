package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by PHAMHUNG on 2/3/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeReturnCreateDto implements Serializable, Parcelable {

    private static final long serialVersionUID = 8895086798968970698L;

    private String distributorId;
    private String customerId;
    private String salesmanId;
    private ExchangeReturnDetailCreateDto[] details;
    // Logic Variable
    private int total;

    public static final Creator<ExchangeReturnCreateDto> CREATOR = new Creator<ExchangeReturnCreateDto>() {
        public ExchangeReturnCreateDto createFromParcel(Parcel in) {
            return new ExchangeReturnCreateDto(in);
        }

        public ExchangeReturnCreateDto[] newArray(int size) {
            return new ExchangeReturnCreateDto[size];
        }
    };

    public ExchangeReturnCreateDto() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(distributorId);
        dest.writeString(customerId);
        dest.writeString(salesmanId);
        dest.writeTypedArray(details, flags);
    }

    public ExchangeReturnCreateDto(Parcel in) {
        distributorId = in.readString();
        customerId = in.readString();
        salesmanId = in.readString();
        details = in.createTypedArray(ExchangeReturnDetailCreateDto.CREATOR);
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(String salesmanId) {
        this.salesmanId = salesmanId;
    }

    public ExchangeReturnDetailCreateDto[] getDetails() {
        return details;
    }

    public void setDetails(ExchangeReturnDetailCreateDto[] details) {
        this.details = details;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExchangeReturnDetailCreateDto implements Serializable, Parcelable {

        private static final long serialVersionUID = 8000317711310529954L;
        public static final Creator<ExchangeReturnDetailCreateDto> CREATOR = new Creator<ExchangeReturnDetailCreateDto>() {
            public ExchangeReturnDetailCreateDto createFromParcel(Parcel in) {
                return new ExchangeReturnDetailCreateDto(in);
            }

            public ExchangeReturnDetailCreateDto[] newArray(int size) {
                return new ExchangeReturnDetailCreateDto[size];
            }
        };
        private String productId;
        private BigDecimal quantity;
        public ExchangeReturnDetailCreateDto(){}
        public ExchangeReturnDetailCreateDto(Parcel in) {
            productId = in.readString();
            String number = in.readString();
            quantity = number == null ? null : new BigDecimal(number);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(productId);
            dest.writeString(quantity == null ? null : quantity.toString());
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }

    }

}