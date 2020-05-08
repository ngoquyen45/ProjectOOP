package com.viettel.backend.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.CustomerEmbed;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.util.entity.SimpleDate;

@Document(collection = "VisitAndOrder")
public class VisitPhoto extends PO {

    private static final long serialVersionUID = -3282388189852006751L;

    private CategoryEmbed distributor;
    private CustomerEmbed customer;
    private UserEmbed createdBy;
    private SimpleDate startTime;

    private boolean closed;
    private String photo;

    public CategoryEmbed getDistributor() {
        return distributor;
    }

    public CustomerEmbed getCustomer() {
        return customer;
    }

    public UserEmbed getCreatedBy() {
        return createdBy;
    }

    public SimpleDate getStartTime() {
        return startTime;
    }

    public boolean isClosed() {
        return closed;
    }

    public String getPhoto() {
        return photo;
    }

    public SimpleDate getCreatedTime() {
        return startTime;
    }

}
