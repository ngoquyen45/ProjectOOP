package com.viettel.backend.dto.category;

import com.viettel.backend.domain.Route;
import com.viettel.backend.dto.common.CategoryDto;

public class RouteDto extends CategoryDto {

    private static final long serialVersionUID = 7395682242004573136L;

    private UserSimpleDto salesman;
    
    public RouteDto(Route route) {
        super(route);
        
        if (route.getSalesman() != null) {
            this.salesman = new UserSimpleDto(route.getSalesman());
        }
    }

    public UserSimpleDto getSalesman() {
        return salesman;
    }

    public void setSalesman(UserSimpleDto salesman) {
        this.salesman = salesman;
    }

}
