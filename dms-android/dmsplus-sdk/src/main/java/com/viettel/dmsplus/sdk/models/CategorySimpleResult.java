package com.viettel.dmsplus.sdk.models;

/**
 * @author PHAMHUNG
 * @since 11/3/2015
 */
public class CategorySimpleResult {

    private CategorySimple[] list;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public CategorySimple[] getList() {
        return list;
    }

    public void setList(CategorySimple[] list) {
        this.list = list;
    }

}
