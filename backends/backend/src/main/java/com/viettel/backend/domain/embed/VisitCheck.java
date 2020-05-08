package com.viettel.backend.domain.embed;

import java.io.Serializable;

import com.viettel.backend.util.entity.SimpleDate;

public class VisitCheck implements Serializable {

    private static final long serialVersionUID = -3176278710067948402L;

    public static final int CHECK_STATUS_FAIL = 0;
    public static final int CHECK_STATUS_PASS = 1;
    public static final int CHECK_STATUS_OTHER = 2;

    private SimpleDate checkDate;
    private UserEmbed storeChecker;
    private int checkStatus;
    private String note;
    private CategoryEmbed deliveryMan;
    private SimpleDate deliveryDate;

    public SimpleDate getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(SimpleDate checkDate) {
        this.checkDate = checkDate;
    }

    public UserEmbed getStoreChecker() {
        return storeChecker;
    }

    public void setStoreChecker(UserEmbed storeChecker) {
        this.storeChecker = storeChecker;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public CategoryEmbed getDeliveryMan() {
        return deliveryMan;
    }

    public void setDeliveryMan(CategoryEmbed deliveryMan) {
        this.deliveryMan = deliveryMan;
    }

    public SimpleDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(SimpleDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

}
