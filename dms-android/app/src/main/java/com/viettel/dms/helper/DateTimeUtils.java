package com.viettel.dms.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtils {

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String formatDate(Date date) {
        return formatDate(date, "dd/MM/yyyy");
    }

    public static String formatTime(Date date) {
        return formatDate(date, "HH:mm");
    }

    public static String formatDateAndTime(Date date) {
        return formatDate(date, "HH:mm dd/MM/yyyy");
    }

    public static String formatDate(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }

//    public static String getCurrentDateInString(String strFormat) {
//        if (strFormat == null) {
//            strFormat = "";
//        }
//
//        Date time = currentDate();
//        SimpleDateFormat timeFormat = new SimpleDateFormat(
//                strFormat, Locale.getDefault());
//        return timeFormat.format(time);
//    }

//    public static String getTimeAgo(Date date, Context ctx) {
//
//        if (date == null) {
//            return null;
//        }
//
//        long time = date.getTime();
//
//        Date curDate = currentDate();
//        long now = curDate.getTime();
//        if (time > now || time <= 0) {
//            return null;
//        }
//
//        int dim = getTimeDistanceInMinutes(time);
//
//        String timeAgo = null;
//
//        if (dim == 0) {
//            timeAgo = ctx.getResources().getString(
//                    R.string.date_util_term_less)
//                    + " "
//                    + ctx.getResources().getString(R.string.date_util_term_a)
//                    + " "
//                    + ctx.getResources().getString(
//                    R.string.date_util_unit_minute);
//        } else if (dim == 1) {
//            return "1 "
//                    + ctx.getResources().getString(
//                    R.string.date_util_unit_minute);
//        } else if (dim >= 2 && dim <= 44) {
//            timeAgo = dim
//                    + " "
//                    + ctx.getResources().getString(
//                    R.string.date_util_unit_minutes);
//        } else if (dim >= 45 && dim <= 89) {
//            timeAgo = ctx.getResources().getString(
//                    R.string.date_util_prefix_about)
//                    + " "
//                    + ctx.getResources()
//                    .getString(R.string.date_util_term_an)
//                    + " "
//                    + ctx.getResources().getString(
//                    R.string.date_util_unit_hour);
//        } else if (dim >= 90 && dim <= 1439) {
//            timeAgo = ctx.getResources().getString(
//                    R.string.date_util_prefix_about)
//                    + " "
//                    + (Math.round(dim / 60))
//                    + " "
//                    + ctx.getResources().getString(
//                    R.string.date_util_unit_hours);
//        } else if (dim >= 1440 && dim <= 2519) {
//            timeAgo = "1 "
//                    + ctx.getResources().getString(
//                    R.string.date_util_unit_day);
//        } else if (dim >= 2520 && dim <= 43199) {
//            timeAgo = (Math.round(dim / 1440))
//                    + " "
//                    + ctx.getResources().getString(
//                    R.string.date_util_unit_days);
//        } else if (dim >= 43200 && dim <= 86399) {
//            timeAgo = ctx.getResources().getString(
//                    R.string.date_util_prefix_about)
//                    + " "
//                    + ctx.getResources().getString(R.string.date_util_term_a)
//                    + " "
//                    + ctx.getResources().getString(
//                    R.string.date_util_unit_month);
//        } else if (dim >= 86400 && dim <= 525599) {
//            timeAgo = (Math.round(dim / 43200))
//                    + " "
//                    + ctx.getResources().getString(
//                    R.string.date_util_unit_months);
//        } else if (dim >= 525600 && dim <= 655199) {
//            timeAgo = ctx.getResources().getString(
//                    R.string.date_util_prefix_about)
//                    + " "
//                    + ctx.getResources().getString(R.string.date_util_term_a)
//                    + " "
//                    + ctx.getResources().getString(
//                    R.string.date_util_unit_year);
//        } else if (dim >= 655200 && dim <= 914399) {
//            timeAgo = ctx.getResources().getString(
//                    R.string.date_util_prefix_over)
//                    + " "
//                    + ctx.getResources().getString(R.string.date_util_term_a)
//                    + " "
//                    + ctx.getResources().getString(
//                    R.string.date_util_unit_year);
//        } else if (dim >= 914400 && dim <= 1051199) {
//            timeAgo = ctx.getResources().getString(
//                    R.string.date_util_prefix_almost)
//                    + " 2 "
//                    + ctx.getResources().getString(
//                    R.string.date_util_unit_years);
//        } else {
//            timeAgo = ctx.getResources().getString(
//                    R.string.date_util_prefix_about)
//                    + " "
//                    + (Math.round(dim / 525600))
//                    + " "
//                    + ctx.getResources().getString(
//                    R.string.date_util_unit_years);
//        }
//
//        return timeAgo + " "
//                + ctx.getResources().getString(R.string.date_util_suffix);
//    }

    private static int getTimeDistanceInMinutes(long time) {
        int timeDistance;
        timeDistance = (int) (currentDate().getTime() - time);
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }

    public static Date truncTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    public static Date convertWithTimezone(TimeZone timeZone, Date localDate) {
        Calendar calLocal = Calendar.getInstance(timeZone);
        calLocal.setTime(localDate);

        Calendar calAfter = Calendar.getInstance();
        calAfter.set(Calendar.YEAR, calLocal.get(Calendar.YEAR));
        calAfter.set(Calendar.MONTH, calLocal.get(Calendar.MONTH));
        calAfter.set(Calendar.DAY_OF_MONTH, calLocal.get(Calendar.DAY_OF_MONTH));
        calAfter.set(Calendar.HOUR_OF_DAY, calLocal.get(Calendar.HOUR_OF_DAY));
        calAfter.set(Calendar.MINUTE, calLocal.get(Calendar.MINUTE));
        calAfter.set(Calendar.MILLISECOND, calLocal.get(Calendar.MILLISECOND));
        return calAfter.getTime();
    }

    public static String changeDateFormat(Date date, String strFormat) {
        if (strFormat == null) {
            strFormat = "";
        }
        if (date == null) return "";
        SimpleDateFormat timeFormat = new SimpleDateFormat(
                strFormat, Locale.getDefault());
        return timeFormat.format(date);
    }

    public static int compare(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
            return c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
        if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH))
            return c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
        return c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
    }
    public static boolean isToday(Date date){
        Calendar today = Calendar.getInstance();
        Calendar anotherDate = Calendar.getInstance();
        anotherDate.setTime(date);

        return today.get(Calendar.YEAR) == anotherDate.get(Calendar.YEAR)
                && today.get(Calendar.MONTH) == anotherDate.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) == anotherDate.get(Calendar.DAY_OF_MONTH);
    }
}
