package com.viettel.backend.dto.category;

import java.util.List;

import com.viettel.backend.dto.common.CategoryCreateDto;
import com.viettel.backend.util.entity.Location;

public class CustomerCreateDto extends CategoryCreateDto {

    private static final long serialVersionUID = 6617041574138019886L;

    private String mobile;
    private String phone;
    private String contact;
    private String email;
    private Location location;

    private String areaId;
    private String customerTypeId;

    private List<String> photos;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getCustomerTypeId() {
        return customerTypeId;
    }

    public void setCustomerTypeId(String customerTypeId) {
        this.customerTypeId = customerTypeId;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

}
