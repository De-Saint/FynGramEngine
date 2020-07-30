/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Managers;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author mac
 */
public class DateManager {

    /**
     *
     */
    public final static long SECOND_MILLIS = 1000;

    /**
     *
     */
    public final static long MINUTE_MILLIS = SECOND_MILLIS * 60;

    /**
     *
     */
    public final static long HOUR_MILLIS = MINUTE_MILLIS * 60;

    /**
     *
     */
    public final static long DAY_MILLIS = HOUR_MILLIS * 24;

    /**
     *
     */
    public final static long YEAR_MILLIS = DAY_MILLIS * 365;

    /**
     *
     */
    public static DateFormat OUT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    /**
     *
     */
    public static DateFormat OUT_TIME_FORMAT = new SimpleDateFormat("H:mm:ss");

    /**
     *
     */
    public static DateFormat OUT_DATETIME_FORMAT = new SimpleDateFormat("d/M/yyyy H:mm:ss");

    /**
     *
     */
    public static DateFormat OUT_TIMESTAMP_FORMAT = new SimpleDateFormat("d/M/yy H:mm:ss.SSS");

    /**
     *
     */
    public static DateFormat IN_DATE_FORMAT = new SimpleDateFormat("d/M/yy");

    /**
     *
     */
    public static DateFormat IN_TIME_FORMAT = new SimpleDateFormat("H:mm:ss");

    /**
     *
     */
    public static DateFormat IN_DATETIME_FORMAT = new SimpleDateFormat("d/M/yy H:mm:ss");

    /**
     *
     */
    public static DateFormat IN_TIMESTAMP_FORMAT = new SimpleDateFormat("d/M/yy H:mm:ss.SSS");

    /**
     *
     */
    public static DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddkkmmss");

    /**
     *
     */
    public static Calendar calendar = new GregorianCalendar();

    static {
        IN_DATE_FORMAT.setLenient(false);
        IN_TIME_FORMAT.setLenient(false);
        IN_DATETIME_FORMAT.setLenient(false);
    }

    /**
     *
     * @param date
     * @return
     */
    public static String readDate(String date) {
        // this function reads out any date with format yyyy-MM-dd
        String[] months = {"Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String dateText = "";
        String superscript = "ᵗʰ";
        try {
            String[] dates = date.split("-");
            String year = dates[0].trim();
            String month = dates[1].trim();
            String day = dates[2].trim();
            int mth = Integer.parseInt(month);
            int dy = Integer.parseInt(day);

            if (dy != 13 && day.charAt(day.length() - 1) == '3') {
                superscript = "rd";
            } else if (dy != 11 && day.charAt(day.length() - 1) == '1') {
                superscript = "st";
            } else if (dy != 12 && day.charAt(day.length() - 1) == '2') {
                superscript = "nd";
            }

            dateText = dy + superscript + " " + months[mth - 1] + " " + year;
        } catch (Exception e) {
            dateText = "N/A";
        }
        return dateText;
    }

    /**
     *
     * @param oldTime
     * @param currentTime
     * @return
     */
    public static String calculateTimeDifference(java.sql.Timestamp oldTime, java.sql.Timestamp currentTime) {
        long milliseconds1 = oldTime.getTime();
        long milliseconds2 = currentTime.getTime();

        long diff = milliseconds2 - milliseconds1;
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);
        long diffWeeks = diff / (7 * 24 * 60 * 60 * 1000);
        long diffMonths = diff / (30 * 24 * 60 * 60 * 1000);

        String difference = "";
        if (diffMonths >= 12) {
            long diffYears = diffMonths / 12;
            difference = diffYears + " y";
            if (diffYears > 1) {
                difference = difference + "s";
            }
        } else if (diffDays >= 7) {
            difference = diffWeeks + " w";
            if (diffWeeks > 1) {
                difference = difference + "s";
            }
        } else if (diffHours >= 24) {
            difference = diffDays + " d";
            if (diffDays > 1) {
                difference = difference + "s";
            }
        } else if (diffMinutes >= 60) {
            difference = diffHours + " h";
            if (diffHours > 1) {
                difference = difference + "s";
            }
        } else if (diffSeconds >= 60) {
            difference = diffMinutes + " m";
            if (diffMinutes > 1) {
                difference = difference + "s";
            }
        } else {
            difference = "just now";
            if (diffSeconds > 10) {
                difference = difference + "s";
            }
        }
        return difference;
    }

    /**
     *
     * @param time
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static String readTime(String time) throws ClassNotFoundException, SQLException {
        String realTime = "";
        String suffix = "A.M";
        String nums[] = time.split(":");
        if (Integer.parseInt(nums[0]) > 12) {
            nums[0] = "" + (Integer.parseInt(nums[0]) - 12);
            suffix = "P.M";
        } else if (Integer.parseInt(nums[0]) == 12) {
            suffix = "P.M";
        }
        realTime = "" + nums[0] + ":" + nums[1] + " " + suffix;
        return realTime;
    }

    /**
     *
     * @param minutes
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static String ConvertMinutesToHours(int minutes) throws ClassNotFoundException, SQLException {
        String realTime = "N/A";
        try {
            int hours = minutes / 60;
            int left_minutes = minutes % 60;
            String lm = "";
            if (left_minutes > 0) {
                lm = left_minutes + " minutes";
            }
            String hrs = "";
            if (hours > 0) {
                hrs = hours + " hours ";
            }
            realTime = hrs + "" + lm;
        } catch (Exception e) {
            return "0";
        }
        if (realTime.trim().equals("")) {
            realTime = "0";
        }
        return realTime;
    }

    /**
     *
     * @param currentTime
     * @param oldTime
     * @return
     */
    public static long calculateTimeDifferenceInHours(java.sql.Timestamp currentTime, java.sql.Timestamp oldTime) {
        long milliseconds1 = oldTime.getTime();
        long milliseconds2 = currentTime.getTime();

        long diff = milliseconds2 - milliseconds1;
        long diffHours = diff / (60 * 60 * 1000);

        return diffHours;
    }

    /**
     *
     * @param Time
     * @return
     */
    public static long convertTimeToHours(java.sql.Timestamp Time) {
        long milliseconds1 = Time.getTime();
        long timeInHours = milliseconds1 / (60 * 60 * 1000);

        return timeInHours;
    }
   
    /**
     *
     * @param startDate
     * @param months
     * @return
     */
    public static java.sql.Date rollMonths(java.util.Date startDate, int months) {
        return rollDate(startDate, Calendar.MONTH, months);
    }
    
    /**
     *
     * @param startDate
     * @param period
     * @param amount
     * @return
     */
    public static java.sql.Date rollDate(java.util.Date startDate, int period, int amount) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(startDate);
        gc.add(period, amount);
        return new java.sql.Date(gc.getTime().getTime());
    }

}
