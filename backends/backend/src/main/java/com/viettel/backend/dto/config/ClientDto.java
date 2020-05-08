package com.viettel.backend.dto.config;

import com.viettel.backend.domain.CalendarConfig;
import com.viettel.backend.domain.Client;
import com.viettel.backend.domain.Config;
import com.viettel.backend.dto.common.CategoryDto;

public class ClientDto extends CategoryDto {

    private static final long serialVersionUID = -7626033792562012411L;

    private ClientConfigDto clientConfig;
    private CalendarConfigDto calendarConfig;

    public ClientDto(Client client, Config config, CalendarConfig calendarConfig) {
        super(client);

        this.clientConfig = new ClientConfigDto(config);
        this.calendarConfig = new CalendarConfigDto(calendarConfig);
    }

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
