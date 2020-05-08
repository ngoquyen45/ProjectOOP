package com.viettel.dmsplus.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.viettel.dmsplus.sdk.network.IsoDateDeserializer;

import java.util.Date;

/**
 * @author PHAMHUNG
 * @since 4/8/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerRegisterInfo {

    private String id;
    private String name;
    private String code;
    private String address;
    private String phone;
    private String mobile;
    private String contact;
    private int status;
    @JsonDeserialize(using = IsoDateDeserializer.class)
    private Date createdTime;
    private CategorySimple customerType;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public CategorySimple getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CategorySimple customerType) {
        this.customerType = customerType;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
