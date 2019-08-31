package com.soteria.neurolab.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateManager {

    /**
     * Converts a date string into a Date type.
     * Returns Null if there was a parsing error.
     * @param dateString the date string to be converted. It must be in yyyy-mm-dd format.
     * @return Date The date object if parsing was successful; null otherwise.
     */
    public static Date convertToDate(String dateString) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return simpleDateFormat.parse(dateString);
    }
}
