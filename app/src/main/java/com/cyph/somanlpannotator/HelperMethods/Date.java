package com.cyph.somanlpannotator.HelperMethods;

import java.util.Calendar;

/**
 * Contains helper functions to allow for date related operations
 * @author Otakenne
 * @since 1
 */
public class Date {
    /**
     * Gets the current date and time in the format YYYY/MM/DD HH:MM:SS:MSC
     * @return The current date and time formatted as YYYY/MM/DD HH:MM:SS:MSC
     */
    public static String getDate(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + "/" + Month.rebaseMonthIndex(calendar.get(Calendar.MONTH)) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + ":" + calendar.get(Calendar.MILLISECOND);
    }

    /**
     * Converts a "YYYY/MM/DD HH:MM:SS:MSC" formatted date string to "YYYYMMDDHHMMSSMSC"
     * This form is sortable
     * @param Date A "YYYY/MM/DD HH:MM:SS:MSC" formatted date string
     * @return A "YYYYMMDDHHMMSSMSC" formatted date string
     */
    public static String convertToSortableDate(String Date) {
        if (Date == null) {return "";}
        if (Date.equals("")) {return "";}
        try {
            String[] calendarDate = Date.split(" ")[0].split("/");
            String[] time = Date.split(" ")[1].split(":");

            String year = calendarDate[0];
            String month = makeTwoDigits(Integer.parseInt(calendarDate[1]));
            String day = makeTwoDigits(Integer.parseInt(calendarDate[2]));
            String hour = makeTwoDigits(Integer.parseInt(time[0]));
            String min = makeTwoDigits(Integer.parseInt(time[1]));
            String sec = makeTwoDigits(Integer.parseInt(time[2]));
            String milliSec = makeThreeDigits(Integer.parseInt(time[3]));

            return year + month + day +
                    hour + min + sec + milliSec;
        } catch (Exception e) {
            return "00000000000000000";
        }
    }

    /**
     * Takes an integer value and prepends/pads "0" if the integer is a single digit
     * @param val Integer to be padded
     * @return Padded integer returned as a string
     */
    public static String makeTwoDigits(int val){
        if (val >= 0 && val < 10){
            return "0" + val;
        }
        return String.valueOf(val);
    }

    /**
     * Takes a string value and prepends/pads "0" if the string is a single letter
     * @param val String to be padded
     * @return Padded string
     */
    public static String makeTwoDigits(String val){
        int valInt = Integer.parseInt(val);
        if (valInt >= 0 && valInt < 10){
            return "0" + valInt;
        }
        return String.valueOf(valInt);
    }

    /**
     * Takes an integer value and prepends/pads "00" if the integer is a single digit
     * or "0" if the integer is a double digit
     * @param val Integer to be padded
     * @return Padded integer returned as a string
     */
    public static String makeThreeDigits(int val){
        if (val >= 0 && val < 10){
            return "00" + val;
        } else if (val > 9 && val < 100 ) {
            return "0" + val;
        }
        return String.valueOf(val);
    }
}
