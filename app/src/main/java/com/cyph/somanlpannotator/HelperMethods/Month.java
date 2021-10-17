package com.cyph.somanlpannotator.HelperMethods;

/**
 * Contains helper functions to allow for month related operations
 * @author Otakenne
 * @since 1
 */
public class Month {
    /**
     * Converts the index of a month to the month's name
     * @param val Index of the month
     * @return Month's name e.g. January
     */
    public static String getMonthName(int val){
        switch (val) {
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
        }
        return "";
    }

    /**
     * Re-bases the index of a month from 0 based to 1 based
     * @param val 0 based index
     * @return 1 based index
     */
    public static String rebaseMonthIndex(int val){
        return String.valueOf(val + 1);
    }
}
