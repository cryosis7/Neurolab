package com.soteria.neurolab.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateManager {

    /**
     * Converts a date string into a Date type.
     * Returns Null if there was a parsing error.
     * @param dateString the date string to be converted. It must be in yyyy-mm-dd format.
     * @return Date The date object if parsing was successful; null otherwise.
     */
    public static Calendar convertToCalendar(String dateString) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(simpleDateFormat.parse(dateString));
            return cal;
        }
        catch (ParseException ex) {
            throw ex;
        }
    }

    public static long convertToMillis(String dateString) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(simpleDateFormat.parse(dateString));
            return cal.getTimeInMillis();
        }
        catch (ParseException ex) {
            throw ex;
        }
    }

    /**
     * Converts a date into a short date string.
     * Use this function for consistency and readability throughout the application
     * @param date a date to convert into a string.
     * @return a String formatted as '13-Aug'.
     */
    public static String getShortDateString(Date date) {
        DateFormat df = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        return df.format(date);
    }

    /**
     * Converts a date into a long date string.
     * Use this function for consistency and readability throughout the application
     * @param date a date to convert into a string.
     * @return a String formatted as '2019-08-13'.
     */
    public static String getLongDateString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return df.format(date);
    }
}
