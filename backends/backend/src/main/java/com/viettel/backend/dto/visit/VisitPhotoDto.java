package com.viettel.backend.dto.visit;

import com.viettel.backend.domain.VisitPhoto;
import com.viettel.backend.dto.category.CustomerSimpleDto;
import com.viettel.backend.dto.category.UserSimpleDto;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.dto.common.DTOSimple;

public class VisitPhotoDto extends DTOSimple {

    private static final long serialVersionUID = -3282388189852006751L;

    private CategorySimpleDto distributor;
    private CustomerSimpleDto customer;
    private UserSimpleDto createdBy;
    private String createdTime;

    private boolean closed;
    private String photo;

    public VisitPhotoDto(VisitPhoto visit) {
        super(visit);

        if (visit.getDistributor() != null) {
            this.distributor = new CategorySimpleDto(visit.getDistributor());
        }

        if (visit.getCustomer() != null) {
            this.customer = new CustomerSimpleDto(visit.getCustomer());
        }

        if (visit.getCreatedBy() != null) {
            this.createdBy = new UserSimpleDto(visit.getCreatedBy());
        }

        this.createdTime = visit.getCreatedTime() != null ? visit.getCreatedTime().getIsoTime() : null;

        this.closed = visit.isClosed();
        this.photo = visit.getPhoto();
    }

    public CategorySimpleDto getDistributor() {
        return distributor;
    }

    public void setDistributor(CategorySimpleDto distributor) {
        this.distributor = distributor;
    }

    public CustomerSimpleDto getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerSimpleDto customer) {
        this.customer = customer;
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

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
