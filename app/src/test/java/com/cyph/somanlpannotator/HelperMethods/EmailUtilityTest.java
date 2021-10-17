package com.cyph.somanlpannotator.HelperMethods;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class EmailUtilityTest {

    @Test
    public void validateEmailTest_wellFormatted() {
        boolean isValidEmail = EmailUtility.validateEmail("annotate@test.com");
        assertThat(isValidEmail, is(equalTo(true)));
    }

    @Test
    public void validateEmailTest_badlyFormatted() {
        boolean isValidEmail = EmailUtility.validateEmail("annotate@.");
        assertThat(isValidEmail, is(equalTo(false)));
    }

    @Test
    public void validateEmailTest_isEmpty() {
        boolean isValidEmail = EmailUtility.validateEmail("");
        assertThat(isValidEmail, is(equalTo(false)));
    }

    @Test
    public void validateEmailTest_isNull() {
        boolean isValidEmail = EmailUtility.validateEmail(null);
        assertThat(isValidEmail, is(equalTo(false)));
    }
}