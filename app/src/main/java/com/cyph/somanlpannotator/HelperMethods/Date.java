package com.cyph.somanlpannotator.HelperMethods;

import java.util.Calendar;

public class Date {
    public static String getDate(){
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.YEAR)) + "/" + Month.MonthBase1(calendar.get(Calendar.MONTH)) + "/" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(calendar.get(Calendar.MINUTE)) + ":" + String.valueOf(calendar.get(Calendar.SECOND)) + ":" + String.valueOf(calendar.get(Calendar.MILLISECOND));
    }

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

    public static String makeTwoDigits(int val){
        if (val >= 0 && val < 10){
            return "0" + String.valueOf(val);
        }
        return String.valueOf(val);
    }

    public static String makeTwoDigits(String val){
        int valInt = Integer.parseInt(val);
        if (valInt >= 0 && valInt < 10){
            return "0" + String.valueOf(valInt);
        }
        return String.valueOf(valInt);
    }

    public static String makeThreeDigits(int val){
        if (val >= 0 && val < 10){
            return "00" + String.valueOf(val);
        } else if (val > 9 && val < 100 ) {
            return "0" + String.valueOf(val);
        }
        return String.valueOf(val);
    }
}
