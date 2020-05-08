package com.viettel.dmsplus.sdk.models;

/**
 * @author PHAMHUNG
 * @since 3/6/2015
 */
public class CustomerListResult {

    private CustomerForVisit[] list;
    private int count;

    public CustomerForVisit[] getList() {
        return list;
    }

    public void setList(CustomerForVisit[] list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
