package com.viettel.backend.dto.schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.viettel.backend.domain.embed.ScheduleItem;

public class ScheduleItemDto implements Serializable {

    private static final long serialVersionUID = -4477763252232660648L;

    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;
    private List<Integer> weeks;

    public ScheduleItemDto() {
    }

    public ScheduleItemDto(ScheduleItem scheduleItem) {
        this.monday = scheduleItem.isMonday();
        this.tuesday = scheduleItem.isTuesday();
        this.wednesday = scheduleItem.isWednesday();
        this.thursday = scheduleItem.isThursday();
        this.friday = scheduleItem.isFriday();
        this.saturday = scheduleItem.isSaturday();
        this.sunday = scheduleItem.isSunday();
        this.weeks = scheduleItem.getWeeks() == null ? null : new ArrayList<Integer>(scheduleItem.getWeeks());
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public List<Integer> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<Integer> weeks) {
        this.weeks = weeks;
    }
    
}
