package com.viettel.backend.dto.category;

import com.viettel.backend.domain.Customer;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.util.entity.Location;

public class CustomerListDto extends CategoryDto {

    private static final long serialVersionUID = -8198585862402781027L;

    private String mobile;
    private String phone;
    private String contact;

    private Location location;

    private int status;
    private UserSimpleDto createdBy;
    private String createdTime;

    private CategorySimpleDto customerType;
    private CategorySimpleDto area;

    private UserSimpleDto salesman;

    public CustomerListDto(Customer customer) {
        super(customer);

        this.mobile = customer.getMobile();
        this.phone = customer.getPhone();
        this.contact = customer.getContact();

        if (customer.getLocation() != null && customer.getLocation().length == 2) {
            this.location = new Location(customer.getLocation());
        }

        this.status = customer.getApproveStatus();
        if (customer.getCreatedBy() != null) {
            this.createdBy = new UserSimpleDto(customer.getCreatedBy());
        }
        if (customer.getCustomerType() != null) {
            this.customerType = new CategorySimpleDto(customer.getCustomerType());
        }
        if (customer.getArea() != null) {
            this.area = new CategorySimpleDto(customer.getArea());
        }
        if (customer.getCreatedTime() != null) {
            this.createdTime = customer.getCreatedTime().getIsoTime();
        }
    }
    
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

    public Location getLocation() {
        return location;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    public CategorySimpleDto getArea() {
        return area;
    }
    
    public void setArea(CategorySimpleDto area) {
        this.area = area;
    }

    public CategorySimpleDto getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CategorySimpleDto customerType) {
        this.customerType = customerType;
    }

    public UserSimpleDto getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserSimpleDto createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public UserSimpleDto getSalesman() {
        return salesman;
    }

    public void setSalesman(UserSimpleDto salesman) {
        this.salesman = salesman;
    }

}
