package com.cyph.somanlpannotator.HelperMethods;

import android.text.TextUtils;

/**
 * Contains helper functions to allow for email related operations
 * @author Otakenne
 * @since 1
 */
public class EmailUtility {
    /**
     * Returns a boolean value on if an email input is valid/well formatted or not
     * @param emailString Email input
     * @return True if email is well formatted or false if not
     */
    public static boolean validateEmail(String emailString) {
        return !TextUtils.isEmpty(emailString) && android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches();
    }
}
