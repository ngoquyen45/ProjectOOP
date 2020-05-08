package com.viettel.dmsplus.sdk.models;

/**
 * @author Thanh
 * @since 10/13/2015
 */
public class VisitCheckList {

    private VisitCheckSimple[] list;
    private int count;

    public VisitCheckSimple[] getList() {
        return list;
    }

    public void setList(VisitCheckSimple[] list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
