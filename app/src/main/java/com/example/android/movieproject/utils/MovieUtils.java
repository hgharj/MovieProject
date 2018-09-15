package com.example.android.movieproject.utils;

import java.util.HashMap;

/**
 * Created by jcgray on 8/26/18.
 */

public class MovieUtils {
    public static String convertYYYY_MM_DD_MiddleEndian(String date) {
        String sYear = date.substring(0, 4);
        int length = date.length();
        int iMonth = Integer.parseInt(date.substring(5, 7));
        String sDay = date.substring(8, 10);
        HashMap monthIntToName = new HashMap();
        monthIntToName.put(1, "January");
        monthIntToName.put(2, "February");
        monthIntToName.put(3, "March");
        monthIntToName.put(4, "April");
        monthIntToName.put(5, "May");
        monthIntToName.put(6, "June");
        monthIntToName.put(7, "July");
        monthIntToName.put(8, "August");
        monthIntToName.put(9, "September");
        monthIntToName.put(10, "October");
        monthIntToName.put(11, "November");
        monthIntToName.put(12, "December");
        String sMonth = monthIntToName.get(iMonth).toString();

        return sMonth + " " + sDay + ", " + sYear;
    }
}
