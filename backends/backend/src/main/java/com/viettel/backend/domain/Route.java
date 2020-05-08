package com.viettel.backend.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.viettel.backend.domain.annotation.UseDistributor;
import com.viettel.backend.domain.embed.UserEmbed;

@UseDistributor
@Document(collection = "Route")
public class Route extends Category {

    private static final long serialVersionUID = -6365294573313110385L;
    
    public static final String COLUMNNAME_SALESMAN = "salesman";
    public static final String COLUMNNAME_SALESMAN_ID = "salesman.id";

    private UserEmbed salesman;

    public UserEmbed getSalesman() {
        return salesman;
    }

    public void setSalesman(UserEmbed salesman) {
        this.salesman = salesman;
    }

}
