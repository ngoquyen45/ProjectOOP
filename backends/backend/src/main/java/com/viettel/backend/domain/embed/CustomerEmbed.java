package com.viettel.backend.domain.embed;

import com.viettel.backend.domain.Customer;

public class CustomerEmbed extends CategoryEmbed {

    private static final long serialVersionUID = -3158993502257758137L;

    private CategoryEmbed area;
    private CategoryEmbed customerType;

    public CustomerEmbed() {
        super();
    }

    public CustomerEmbed(Customer customer) {
        super(customer);

        this.area = customer.getArea();
        this.customerType = customer.getCustomerType();
    }
    
    public CategoryEmbed getArea() {
        return area;
    }

    public void setArea(CategoryEmbed area) {
        this.area = area;
    }

    public CategoryEmbed getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CategoryEmbed customerType) {
        this.customerType = customerType;
    }

}
