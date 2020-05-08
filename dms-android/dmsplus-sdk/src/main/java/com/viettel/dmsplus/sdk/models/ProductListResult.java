package com.viettel.dmsplus.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author PHAMHUNG
 * @since 14/4/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductListResult {
    private int count;
    private Product[] list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Product[] getList() {
        return list;
    }

    public void setList(Product[] list) {
        this.list = list;
    }
}
