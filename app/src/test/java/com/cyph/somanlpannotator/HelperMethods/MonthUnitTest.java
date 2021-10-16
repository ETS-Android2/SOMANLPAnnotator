package com.cyph.somanlpannotator.HelperMethods;

import com.cyph.somanlpannotator.HelperMethods.Month;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MonthUnitTest {
    @Test
    public void MonthBase1Test() {
        int input = 2;
        String output = Month.MonthBase1(input);
        assertThat(output, is(equalTo("3")));
    }

    @Test
    public void MonthTest() {
        String month = Month.Month(2);
        assertThat(month, is(equalTo("March")));
    }
}
