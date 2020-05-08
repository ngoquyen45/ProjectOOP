package com.viettel.backend.util.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.viettel.backend.util.DateTimeUtils;

public class SimpleDate implements Comparable<SimpleDate>, Serializable {

    private static final long serialVersionUID = 2254942600391254216L;

    /**
     * example: 2015
     */
    private int year;

    /**
     * 0 -> 11
     */
    private int month;

    /**
     * 1 -> 31
     */
    private int date;

    /**
     * hour_of_day: 0 -> 23
     */
    private int hour;

    /**
     * 0 -> 59
     */
    private int minute;

    /**
     * 0 -> 59
     */
    private int second;

    private long value;

    public SimpleDate() {
        this(0, 0, 1, 0, 0, 0);
    }

    public SimpleDate(SimpleDate date) {
        this(date.getYear(), date.getMonth(), date.getDate(), date.getHour(), date.getMinute(), date.getSecond());
    }

    public SimpleDate(int year, int month, int date) {
        this(year, month, date, 0, 0, 0);
    }

    public SimpleDate(int year, int month, int date, int hour, int minute, int second) {
        if (!isValidData(year, month, date, hour, minute, second)) {
            throw new IllegalArgumentException("data not valid");
        }

        this.year = year;
        this.month = month;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.second = second;

        updateValue();
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        if (!isValidData(0, 0, date, 0, 0, 0)) {
            throw new IllegalArgumentException("data not valid");
        }

        this.date = date;

        updateValue();
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        if (!isValidData(0, month, 1, 0, 0, 0)) {
            throw new IllegalArgumentException("data not valid");
        }

        this.month = month;

        updateValue();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        if (!isValidData(year, 0, 1, 0, 0, 0)) {
            throw new IllegalArgumentException("data not valid");
        }

        this.year = year;

        updateValue();
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        if (!isValidData(0, 0, 1, hour, 0, 0)) {
            throw new IllegalArgumentException("data not valid");
        }

        this.hour = hour;

        updateValue();
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        if (!isValidData(0, 0, 1, 0, minute, 0)) {
            throw new IllegalArgumentException("data not valid");
        }

        this.minute = minute;

        updateValue();
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        if (!isValidData(0, 0, 1, 0, 0, second)) {
            throw new IllegalArgumentException("data not valid");
        }

        this.second = second;

        updateValue();
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        // DO NOTHING
    }

    public int getDayOfWeek() {
        return getCalendar().get(Calendar.DAY_OF_WEEK);
    }

    public String getIsoMonth() {
        return String.format("%04d-%02d", year, month + 1);
    }

    public String getIsoDate() {
        return String.format("%04d-%02d-%02d", year, month + 1, date);
    }

    public String getIsoTime() {
        return String.format("%04d-%02d-%02dT%02d:%02d:%02d", year, month + 1, date, hour, minute, second);
    }

    public Date getDateObject() {
        return getCalendar().getTime();
    }

    /**
     * Compares two Dates for ordering.
     *
     * @param anotherDate
     *            the <code>Date</code> to be compared.
     * @return the value <code>0</code> if the argument Date is equal to this
     *         Date; a value less than <code>0</code> if this Date is before the
     *         Date argument; and a value greater than <code>0</code> if this
     *         Date is after the Date argument.
     * @since 1.2
     * @exception NullPointerException
     *                if <code>anotherDate</code> is null.
     */
    public int compareTo(SimpleDate anotherDate) {
        long thisTime = getValue();
        long anotherTime = anotherDate.getValue();
        return (thisTime < anotherTime ? -1 : (thisTime == anotherTime ? 0 : 1));
    }

    public String format(String pattern) {
        return new SimpleDateFormat(pattern).format(getDateObject());
    }

    private void updateValue() {
        this.value = (year * 10000000000l) + (month * 100000000) + (date * 1000000) + (hour * 10000) + (minute * 100)
                + second;
    }

    private boolean isValidData(int year, int month, int date, int hour, int minute, int second) {
        if (year < 0) {
            return false;
        }

        if (month < 0 || month > 11) {
            return false;
        }

        if (date < 1 || date > 31) {
            return false;
        }

        if (hour < 0 || hour > 23) {
            return false;
        }

        if (minute < 0 || minute > 59) {
            return false;
        }

        if (second < 0 || second > 59) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return getIsoTime();
    }

    @Override
    public int hashCode() {
        return getIsoTime().hashCode();
    }

    private Calendar getCalendar() {
        Calendar c = Calendar.getInstance();
        c.set(year, month, date, hour, minute, second);

        return c;
    }

    public static SimpleDate truncDate(SimpleDate date) {
        return new SimpleDate(date.getYear(), date.getMonth(), date.getDate());
    }

    public static long getDuration(SimpleDate from, SimpleDate to) {
        if (from == null || to == null) {
            return 0;
        }

        return to.getCalendar().getTime().getTime() - from.getCalendar().getTime().getTime();
    }

    public static class Period implements Serializable {

        private static final long serialVersionUID = -8377652259670440568L;

        private SimpleDate fromDate;
        private SimpleDate toDate;

        public Period(SimpleDate fromDate, SimpleDate toDate) {
            if (fromDate == null || toDate == null) {
                throw new IllegalArgumentException();
            }

            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        public SimpleDate getFromDate() {
            return fromDate;
        }

        public void setFromDate(SimpleDate fromDate) {
            this.fromDate = fromDate;
        }

        public SimpleDate getToDate() {
            return toDate;
        }

        public void setToDate(SimpleDate toDate) {
            this.toDate = toDate;
        }

        public Period getCopy() {
            return new Period(new SimpleDate(fromDate), new SimpleDate(toDate));
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public String toString() {
            return fromDate.getIsoTime() + '-' + toDate.getIsoTime();
        }

        public List<SimpleDate> getDates() {
            List<SimpleDate> dates = new LinkedList<>();
            SimpleDate temp = this.getFromDate();
            while (temp.compareTo(this.getToDate()) < 0) {
                dates.add(temp);
                temp = DateTimeUtils.addDays(temp, 1);
            }
            
            return dates;
        }

    }
}
