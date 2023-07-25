package com.example.finalproject;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {

    private static final String PREF_NAME = "IncrementedValuePrefs";
    private static final String KEY_INCREMENTED_VALUE = "incremented_value";

    public static int getIncrementedValue(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int currentValue = prefs.getInt(KEY_INCREMENTED_VALUE, 0);

        // Increment the value and store it back in SharedPreferences
        int incrementedValue = currentValue + 1;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_INCREMENTED_VALUE, incrementedValue);
        editor.apply();

        return incrementedValue;
    }
}
