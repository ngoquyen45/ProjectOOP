package com.viettel.backend.dto.category;

import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.embed.CustomerEmbed;
import com.viettel.backend.dto.common.CategorySimpleDto;

public class CustomerSimpleDto extends CategorySimpleDto {

    private static final long serialVersionUID = 1652073192406165389L;

    private CategorySimpleDto area;
    private CategorySimpleDto customerType;

    public CustomerSimpleDto(CustomerEmbed customer) {
        super(customer);

        if (customer.getArea() != null) {
            this.area = new CategorySimpleDto(customer.getArea());
        }

        if (customer.getCustomerType() != null) {
            this.customerType = new CategorySimpleDto(customer.getCustomerType());
        }
    }
    
    public CustomerSimpleDto(Customer customer) {
        this(new CustomerEmbed(customer));
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

}
