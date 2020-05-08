package com.viettel.backend.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.util.Assert;

import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

public final class DateTimeUtils {
    
    private static SimpleDate create(Date _date) {
        if (_date == null) {
            throw new IllegalArgumentException("date not valid");
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(_date);

            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int date = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);
            
            return new SimpleDate(year, month, date, hour, minute, second);
        }
    }

    public static final long MILLISECONDS_BY_DAY = 24 * 60 * 60 * 1000;
    public static final int DEFAULT_MINIMAL_DAYS_IN_FIRST_WEEK = 4;
    public static final int DEFAULT_FIRST_DAY_OF_WEEK = Calendar.MONDAY;

    public static int getWeekOfYear(SimpleDate date, int firstDayOfWeek, int minimalDaysInFirstWeek) {
        Calendar cal = Calendar.getInstance();
        cal.setMinimalDaysInFirstWeek(minimalDaysInFirstWeek);
        cal.setFirstDayOfWeek(firstDayOfWeek);
        cal.setTime(date.getDateObject());
        return cal.get(Calendar.WEEK_OF_YEAR);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Adds a number of minutes to a date returning a new object.
     * The original date object is unchanged.
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new date object with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static SimpleDate addMinutes(SimpleDate date, int amount) {
        return create(DateUtils.addMinutes(date.getDateObject(), amount));
    }

    /**
     * Adds a number of days to a date returning a new object. The original date
     * object is unchanged.
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to add, may be negative
     * @return the new date object with the amount added
     * @throws IllegalArgumentException
     *             if the date is null
     */
    public static SimpleDate addDays(SimpleDate date, int amount) {
        return create(DateUtils.addDays(date.getDateObject(), amount));
    }

    /**
     * Adds a number of weeks to a date returning a new object. The original
     * date object is unchanged.
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to add, may be negative
     * @return the new date object with the amount added
     * @throws IllegalArgumentException
     *             if the date is null
     */
    public static SimpleDate addWeeks(SimpleDate date, int amount) {
        return create(DateUtils.addWeeks(date.getDateObject(), amount));
    }

    /**
     * Adds a number of months to a date returning a new object. The original
     * date object is unchanged.
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to add, may be negative
     * @return the new date object with the amount added
     * @throws IllegalArgumentException
     *             if the date is null
     */
    public static SimpleDate addMonths(SimpleDate date, int amount) {
        return create(DateUtils.addMonths(date.getDateObject(), amount));
    }

    /**
     * <p>
     * Truncate this date, leaving the field specified as the most significant
     * field.
     * </p>
     *
     * <p>
     * For example, if you had the datetime of 28 Mar 2002 13:45:01.231, if you
     * passed with HOUR, it would return 28 Mar 2002 13:00:00.000. If this was
     * passed with MONTH, it would return 1 Mar 2002 0:00:00.000.
     * </p>
     * 
     * @param date
     *            the date to work with
     * @param field
     *            the field from <code>Calendar</code> or
     *            <code>SEMI_MONTH</code>
     * @return the rounded date
     * @throws IllegalArgumentException
     *             if the date is <code>null</code>
     * @throws ArithmeticException
     *             if the year is over 280 million
     */
    public static SimpleDate truncate(SimpleDate date, int field) {
        Assert.notNull(date, "The date must not be null");
        
        return create(DateUtils.truncate(date.getDateObject(), field));
    }

    public static final SimpleDate getToday() {
        return truncate(getCurrentTime(), Calendar.DATE);
    }

    public static final SimpleDate getTomorrow() {
        return addDays(getToday(), 1);
    }

    public static final SimpleDate getFirstOfThisMonth() {
        return truncate(getCurrentTime(), Calendar.MONTH);
    }

    public static final SimpleDate getFirstOfLastMonth() {
        return addMonths(getFirstOfThisMonth(), -1);
    }

    public static final SimpleDate getCurrentTime() {
        return create(new Date());
    }

    public static final boolean isSameYear(SimpleDate date1, SimpleDate date2) {
        Assert.notNull(date1, "The date must not be null");
        Assert.notNull(date2, "The date must not be null");

        return date1.getYear() == date2.getYear();
    }

    // ** PERIOD **
    public static final Period getPeriodOneDay(SimpleDate date) {
        SimpleDate fromDate = truncate(date, Calendar.DATE);
        SimpleDate toDate = addDays(fromDate, 1);

        return new Period(fromDate, toDate);
    }

    public static final Period getPeriodToday() {
        return getPeriodOneDay(getCurrentTime());
    }

    public static final Period getPeriodLastDays(int numberDays) {
        SimpleDate fromDate = DateTimeUtils.addDays(DateTimeUtils.getToday(), -numberDays);
        SimpleDate toDate = DateTimeUtils.getTomorrow();

        return new Period(fromDate, toDate);
    }

    public static final Period getPeriodThisMonthUntilToday() {
        SimpleDate fromDate = DateTimeUtils.getFirstOfThisMonth();
        SimpleDate toDate = DateTimeUtils.getTomorrow();
        
        return new Period(fromDate, toDate);
    }
    
    public static final Period getPeriodThisMonth() {
        SimpleDate fromDate = DateTimeUtils.getFirstOfThisMonth();
        SimpleDate toDate = DateTimeUtils.addMonths(fromDate, 1);
        
        return new Period(fromDate, toDate);
    }

    public static final Period getPeriodLastMonth() {
        return getPeriodLastsMonth(1);
    }
    
    public static final Period getPeriodLastsMonth(int numberMonths) {
        SimpleDate fromDate = DateTimeUtils.addMonths(DateTimeUtils.getFirstOfThisMonth(), -numberMonths);
        SimpleDate toDate = DateTimeUtils.addMonths(fromDate, 1);
        
        return new Period(fromDate, toDate);

    }

    public static final Period getPeriodByMonth(int month, int year) {
        SimpleDate fromDate = new SimpleDate(year, month, 1);
        SimpleDate toDate = DateTimeUtils.addMonths(fromDate, 1);
        
        return new Period(fromDate, toDate);
    }
}
