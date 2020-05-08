package com.viettel.backend.dto.category;

import java.util.List;

import com.viettel.backend.domain.Customer;

public class CustomerDto extends CustomerListDto {

    private static final long serialVersionUID = 6617041574138019886L;

    private String email;
    private List<String> photos;
    
    public CustomerDto(Customer customer) {
        super(customer);

        this.email = customer.getEmail();
        this.photos = customer.getPhotos();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

}
