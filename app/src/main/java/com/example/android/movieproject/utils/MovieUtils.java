package com.example.android.movieproject.utils;

import java.util.HashMap;

/**
 * Created by jcgray on 8/26/18.
 */

public final class MovieUtils {
    private static final String JANUARY = "January";
    private static final String FEBRUARY = "February";
    private static final String MARCH = "March";
    private static final String APRIL = "April";
    private static final String MAY = "May";
    private static final String JUNE = "June";
    private static final String JULY = "July";
    private static final String AUGUST = "August";
    private static final String SEPTEMBER = "September";
    private static final String OCTOBER = "October";
    private static final String NOVEMBER = "November";
    private static final String DECEMBER = "December";

    public static String convertYYYY_MM_DD_MiddleEndian(String date) {
        String sYear = date.substring(0, 4);
        int iMonth = Integer.parseInt(date.substring(5, 7));
        String sDay = date.substring(8, 10);
        HashMap monthIntToName = new HashMap();
        monthIntToName.put(1, JANUARY);
        monthIntToName.put(2, FEBRUARY);
        monthIntToName.put(3, MARCH);
        monthIntToName.put(4, APRIL);
        monthIntToName.put(5, MAY);
        monthIntToName.put(6, JUNE);
        monthIntToName.put(7, JULY);
        monthIntToName.put(8, AUGUST);
        monthIntToName.put(9, SEPTEMBER);
        monthIntToName.put(10, OCTOBER);
        monthIntToName.put(11, NOVEMBER);
        monthIntToName.put(12, DECEMBER);
        String sMonth = monthIntToName.get(iMonth).toString();

        return sMonth + " " + sDay + ", " + sYear;
    }
}
