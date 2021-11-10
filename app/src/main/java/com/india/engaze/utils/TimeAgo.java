package com.india.engaze.utils;

import android.annotation.SuppressLint;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class TimeAgo {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(String timestamp) {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        long time;
        try {
            time = df.parse(timestamp).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }


        final long diff = now - time - (5 * HOUR_MILLIS) - (30 * MINUTE_MILLIS);

        if (diff < MINUTE_MILLIS) {
            return "Just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "A minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "An hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }


    public static String getMinutesPassed(String timestamp) {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        long time;
        try {
            time = df.parse(timestamp).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }
        final long diff = now - time - (5 * HOUR_MILLIS) - (30 * MINUTE_MILLIS);
        return diff / MINUTE_MILLIS + " minutes";
    }


    public static Long calculateRideMinutes(String startTime) {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        long time;
        try {
            time = df.parse(startTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return 0L;
        }
        final long diff = now - time - (5 * HOUR_MILLIS) - (30 * MINUTE_MILLIS);
        return diff / MINUTE_MILLIS;
    }

    private static int getHoursPast(String timestamp) {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        long time;
        try {
            time = df.parse(timestamp).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return 0;
        }

        final long diff = now - time - (5 * HOUR_MILLIS) - (30 * MINUTE_MILLIS);

        return (int) (diff / HOUR_MILLIS);

    }

    public static String getPerfectTime(String timestamp) {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        @SuppressLint("SimpleDateFormat") DateFormat indianClock = new SimpleDateFormat("EEE, MMM dd, hh:mm aa");
        Date parse;
        String format;
        try {
            parse = df.parse(timestamp);
            format = indianClock.format(parse);
            return format;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getWeekDay() {
        Timber.e("Day: " + Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }


    public static boolean isOpen(String start, String end) {

        long cur, open, close;

        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("HH:mm:ss");
        try {
            Date date = new Date();
            long hour = date.getHours();
            long minute = date.getMinutes();
            long seconds = date.getSeconds();

            cur = hour * HOUR_MILLIS + minute * MINUTE_MILLIS + seconds * 1000;
            open = df.parse(start).getTime() + (5 * HOUR_MILLIS) + (30 * MINUTE_MILLIS);
            close = df.parse(end).getTime() + (5 * HOUR_MILLIS) + (30 * MINUTE_MILLIS);
            return cur > open && cur < close;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isOpen(Long weekbreakdown) {
        return (weekbreakdown & (1 << getWeekDay())) > 0;
    }

    public static String getTimeString(String openTime, String closeTime) {
        String[] open = openTime.split(":");
        String[] close = closeTime.split(":");

        boolean isAm = true;
        Long hour = Long.valueOf(open[0]);
        if (hour > 12) {
            isAm = false;
            hour -= 12;
        }
        StringBuilder timings = new StringBuilder();
        timings.append(hour).append(":").append(open[1]);

        if (isAm) timings.append(" am - ");
        else timings.append(" pm - ");

        hour = Long.valueOf(close[0]);
        isAm = true;
        if (hour > 12) {
            isAm = false;
            hour -= 12;
        }
        timings.append(hour).append(":").append(close[1]);
        if (isAm) timings.append(" am");
        else timings.append(" pm");
        return timings.toString();
    }


    public static boolean isReturnAvailable(String timestamp, Long maxReturnHours) {
        int hours = getHoursPast(timestamp);
        return hours <= maxReturnHours;
    }

    public static String getShortTime(String timestamp) {

        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        @SuppressLint("SimpleDateFormat") DateFormat indianClock = new SimpleDateFormat("hh:mm aa");
        Date parse;
        String format;
        try {
            parse = df.parse(timestamp);
            format = indianClock.format(parse);
            return format;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatDateTime(long timeInMillis) {
        if(isToday(timeInMillis)) {
            return formatTime(timeInMillis);
        } else {
            return formatDate(timeInMillis);
        }
    }

    public static String formatDate(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static String formatTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static boolean isToday(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String date = dateFormat.format(timeInMillis);
        return date.equals(dateFormat.format(System.currentTimeMillis()));
    }

}