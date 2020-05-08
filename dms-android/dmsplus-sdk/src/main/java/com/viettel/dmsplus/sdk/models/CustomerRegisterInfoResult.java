package com.viettel.dmsplus.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by PHAMHUNG on 4/8/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerRegisterInfoResult {
    private CustomerRegisterInfo[] list;
    private int count;

    public CustomerRegisterInfo[] getList() {
        return list;
    }

    public void setList(CustomerRegisterInfo[] list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
