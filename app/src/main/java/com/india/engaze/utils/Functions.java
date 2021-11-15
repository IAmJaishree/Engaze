package com.india.engaze.utils;

public class Functions {



    public static boolean isValidEmail(String email) {

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}