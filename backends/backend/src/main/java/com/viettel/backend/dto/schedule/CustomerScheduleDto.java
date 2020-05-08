package com.viettel.backend.dto.schedule;

import com.viettel.backend.domain.Customer;
import com.viettel.backend.dto.category.CustomerDto;

public class CustomerScheduleDto extends CustomerDto {

    private static final long serialVersionUID = 6025241488260901007L;

    private ScheduleDto schedule;

    public CustomerScheduleDto(Customer customer) {
        super(customer);
        if (customer.getSchedule() != null) {
            this.schedule = new ScheduleDto(customer.getSchedule());
        }
    }

    public ScheduleDto getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleDto schedule) {
        this.schedule = schedule;
    }

}
