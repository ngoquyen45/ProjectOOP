package com.viettel.backend.domain.embed;

import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.annotation.Transient;

public class ScheduleItem implements Serializable {

    private static final long serialVersionUID = -5890415709083121260L;

    public static final String COLUMNNAME_WEEKS = "weeks";
    public static final String getDayColumnname(int dayOfWeek) {
        switch (dayOfWeek) {
        case Calendar.MONDAY:
            return COLUMNNAME_MONDAY;
        case Calendar.TUESDAY:
            return COLUMNNAME_TUESDAY;
        case Calendar.WEDNESDAY:
            return COLUMNNAME_WEDNESDAY;
        case Calendar.THURSDAY:
            return COLUMNNAME_THURSDAY;
        case Calendar.FRIDAY:
            return COLUMNNAME_FRIDAY;
        case Calendar.SATURDAY:
            return COLUMNNAME_SATURDAY;
        case Calendar.SUNDAY:
            return COLUMNNAME_SUNDAY;
        default:
            throw new IllegalArgumentException();
        }
    }
    
    private static final String COLUMNNAME_MONDAY = "monday";
    private static final String COLUMNNAME_TUESDAY = "tuesday";
    private static final String COLUMNNAME_WEDNESDAY = "wednesday";
    private static final String COLUMNNAME_THURSDAY = "thursday";
    private static final String COLUMNNAME_FRIDAY = "friday";
    private static final String COLUMNNAME_SATURDAY = "saturday";
    private static final String COLUMNNAME_SUNDAY = "sunday";

    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;
    private List<Integer> weeks;

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
    
    @Transient
    public List<Integer> getDays() {
        List<Integer> days = new LinkedList<Integer>();
        if (isMonday()) {
            days.add(Calendar.MONDAY);
        }
        
        if (isTuesday()) {
            days.add(Calendar.TUESDAY);
        }
        
        if (isWednesday()) {
            days.add(Calendar.WEDNESDAY);
        }
        
        if (isThursday()) {
            days.add(Calendar.THURSDAY);
        }
        
        if (isFriday()) {
            days.add(Calendar.FRIDAY);
        }
        
        if (isSaturday()) {
            days.add(Calendar.SATURDAY);
        }
        
        if (isSunday()) {
            days.add(Calendar.SUNDAY);
        };
        return days;
    }
    
    @Transient
    public void setDays(List<Integer> days) {
        setMonday(false);
        setTuesday(false);
        setWednesday(false);
        setThursday(false);
        setFriday(false);
        setSaturday(false);
        setSunday(false);
        
        if (days != null && !days.isEmpty()) {
            if (days.contains(Calendar.MONDAY)) {
                setMonday(true);
            }
            if (days.contains(Calendar.TUESDAY)) {
                setTuesday(true);
            }
            if (days.contains(Calendar.WEDNESDAY)) {
                setWednesday(true);
            }
            if (days.contains(Calendar.THURSDAY)) {
                setThursday(true);
            }
            if (days.contains(Calendar.FRIDAY)) {
                setFriday(true);
            }
            if (days.contains(Calendar.SATURDAY)) {
                setSaturday(true);
            }
            if (days.contains(Calendar.SUNDAY)) {
                setSunday(true);
            }
        }
    }
    
    public List<Integer> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<Integer> weeks) {
        this.weeks = weeks;
    }
    
}
