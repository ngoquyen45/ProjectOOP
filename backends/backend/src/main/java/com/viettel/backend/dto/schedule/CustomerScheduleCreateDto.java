package com.viettel.backend.dto.schedule;

import com.viettel.backend.dto.common.DTOSimple;

public class CustomerScheduleCreateDto extends DTOSimple {

    private static final long serialVersionUID = 6025241488260901007L;
    
    private ScheduleDto schedule;

    public CustomerScheduleCreateDto() {
        super((String) null);
    }

    public ScheduleDto getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleDto schedule) {
        this.schedule = schedule;
    }
    
    
    
}
