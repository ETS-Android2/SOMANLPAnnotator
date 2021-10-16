package com.cyph.somanlpannotator.HelperMethods;

import com.cyph.somanlpannotator.HelperMethods.Date;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DateUnitTest {
    @Test
    public void convertToSortableDateTest() {
        String date = "2012/10/2 10:23:34:452";
        String convertDate = Date.convertToSortableDate(date);
        assertThat(convertDate, is(equalTo("20121002102334452")));
    }

    @Test
    public void makeTwoDigitsIntTest() {
        String twoDigits = Date.makeTwoDigits(3);
        assertThat(twoDigits, is(equalTo("03")));
    }

    @Test
    public void makeTwoDigitsStringTest() {
        String twoDigits = Date.makeTwoDigits("3");
        assertThat(twoDigits, is(equalTo("03")));
    }

    @Test
    public void makeThreeDigitsTest() {
        String threeDigits = Date.makeThreeDigits(8);
        assertThat(threeDigits, is(equalTo("008")));
    }
}
