package com.cyph.somanlpannotator.HelperMethods;

import android.text.TextUtils;

public class EmailUtility {
    public static boolean validateEmail(String emailString) {
        return !TextUtils.isEmpty(emailString) && android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches();
    }
}
