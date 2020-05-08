package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * @author Thanh
 * @since 2/9/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RevenueByMonthResult implements Parcelable {

    public static final Creator<RevenueByMonthResult> CREATOR = new Creator<RevenueByMonthResult>() {
        public RevenueByMonthResult createFromParcel(Parcel in) {
            return new RevenueByMonthResult(in);
        }

        public RevenueByMonthResult[] newArray(int size) {
            return new RevenueByMonthResult[size];
        }
    };

    @JsonProperty("list")
    private RevenueByMonthItem[] items;
    private int count;

    public RevenueByMonthResult() {
    }

    public RevenueByMonthResult(Parcel in) {
        items = in.createTypedArray(RevenueByMonthItem.CREATOR);
        count = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(items, flags);
        dest.writeInt(count);
    }

    public RevenueByMonthItem[] getItems() {
        return items;
    }

    public void setItems(RevenueByMonthItem[] items) {
        this.items = items;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RevenueByMonthItem extends CustomerSimple {

        public static Creator<RevenueByMonthItem> CREATOR = new Creator<RevenueByMonthItem>() {
            public RevenueByMonthItem createFromParcel(Parcel in) {
                return new RevenueByMonthItem(in);
            }

            public RevenueByMonthItem[] newArray(int size) {
                return new RevenueByMonthItem[size];
            }
        };

        public static Comparator<RevenueByMonthItem> createRevenueComparator() {
            return new Comparator<RevenueByMonthItem>() {
                @Override
                public int compare(RevenueByMonthItem left, RevenueByMonthItem right) {
                    if (right == null && left == null) {
                        return 0;
                    }
                    if (left == null || left.getSalesResult() == null) {
                        return 1;
                    }
                    if (right == null || right.getSalesResult() == null) {
                        return -1;
                    }
                    BigDecimal lr = left.getSalesResult().getRevenue();
                    if (lr == null) {
                        if (right.getSalesResult().getRevenue() == null) {
                            return 0;
                        }
                        return 1;
                    }
                    if (right.getSalesResult().getRevenue() == null) {
                        return -1;
                    }
                    return right.getSalesResult().getRevenue().compareTo(lr);
                }
            };
        }

        public static Comparator<RevenueByMonthItem> createProductivityComparator() {
            return new Comparator<RevenueByMonthItem>() {
                @Override
                public int compare(RevenueByMonthItem left, RevenueByMonthItem right) {
                    if (right == null && left == null) {
                        return 0;
                    }
                    if (left == null || left.getSalesResult() == null) {
                        return 1;
                    }
                    if (right == null || right.getSalesResult() == null) {
                        return -1;
                    }
                    BigDecimal lp = left.getSalesResult().getProductivity();
                    if (lp == null) {
                        if (right.getSalesResult().getRevenue() == null) {
                            return 0;
                        }
                        return 1;
                    }
                    if (right.getSalesResult().getRevenue() == null) {
                        return -1;
                    }
                    return right.getSalesResult().getProductivity().compareTo(lp);
                }
            };
        }

        private SalesResult salesResult;

        public RevenueByMonthItem() {
        }

        public RevenueByMonthItem(Parcel in) {
            super(in);

            salesResult = in.readParcelable(SalesResult.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            dest.writeParcelable(salesResult, flags);
        }

        public SalesResult getSalesResult() {
            return salesResult;
        }

        public void setSalesResult(SalesResult salesResult) {
            this.salesResult = salesResult;
        }
    }
}
