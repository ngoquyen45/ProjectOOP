package com.viettel.dmsplus.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by PHAMHUNG on 4/15/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExhibitionResult {
    ExhibitionDto[] list;
    int count;

    public ExhibitionDto[] getList() {
        return list;
    }

    public void setList(ExhibitionDto[] list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
