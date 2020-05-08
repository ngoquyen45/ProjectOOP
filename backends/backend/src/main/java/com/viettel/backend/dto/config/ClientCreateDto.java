package com.viettel.backend.dto.config;

import com.viettel.backend.dto.common.CategoryCreateDto;

public class ClientCreateDto extends CategoryCreateDto {

    private static final long serialVersionUID = -7626033792562012411L;

    private ClientConfigDto clientConfig;
    private CalendarConfigDto calendarConfig;

    public ClientConfigDto getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfigDto clientConfig) {
        this.clientConfig = clientConfig;
    }

    public CalendarConfigDto getCalendarConfig() {
        return calendarConfig;
    }

    public void setCalendarConfig(CalendarConfigDto calendarConfig) {
        this.calendarConfig = calendarConfig;
    }

}
