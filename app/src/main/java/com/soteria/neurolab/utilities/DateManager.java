package com.soteria.neurolab.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateManager {

    /**
     * Converts a given date string into the number of milliseconds since the epoch.
     * @param dateString A date in the format of '2019-04-20'
     * @return long value representing milliseconds since epoch.
     * @throws ParseException If there is a formatting error in the string.
     */
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
     * Converts a date into a long date string.
     * Use this function for consistency and readability throughout the application
     * @param date a date to convert into a string.
     * @return a String formatted as '2019-08-13'.
     */
    public static String getDateString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return df.format(date);
    }
}
