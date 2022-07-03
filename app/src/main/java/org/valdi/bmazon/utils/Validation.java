package org.valdi.bmazon.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class Validation {

    public static boolean validatePassword(final String password) {
        return validateFields(password) && password.length() >= 8;
    }

    public static boolean validateEmail(final String email) {
        return validateFields(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean validateFields(final String name) {
        return !TextUtils.isEmpty(name);
    }

}
