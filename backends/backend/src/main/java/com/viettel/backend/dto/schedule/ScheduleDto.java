package com.viettel.backend.dto.schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.viettel.backend.domain.embed.Schedule;
import com.viettel.backend.domain.embed.ScheduleItem;

public class ScheduleDto implements Serializable {

    private static final long serialVersionUID = 4408779711806179090L;

    public String routeId;
    public List<ScheduleItemDto> items;

    public ScheduleDto() {
    }

    public ScheduleDto(Schedule schedule) {
        this.routeId = schedule.getRouteId().toString();
        if (schedule.getItems() != null) {
            this.items = new ArrayList<ScheduleItemDto>(schedule.getItems().size());
            for (ScheduleItem item : schedule.getItems()) {
                this.items.add(new ScheduleItemDto(item));
            }
        }
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public List<ScheduleItemDto> getItems() {
        return items;
    }

    public void setItems(List<ScheduleItemDto> items) {
        this.items = items;
    }

}
