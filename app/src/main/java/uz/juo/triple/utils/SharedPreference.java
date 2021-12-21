package uz.juo.triple.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {
    SharedPreferences prefs;
    private static SharedPreference sharePreference;
    SharedPreferences.Editor editor;

    public static SharedPreference getInstance(Context context) {
        if (sharePreference != null)
            return sharePreference;
        else return sharePreference = new SharedPreference(context);
    }

    private SharedPreference(Context context) {
        prefs = context.getSharedPreferences(getClass().getName(), Context.MODE_PRIVATE);
    }


    public void setHasAlarm(boolean hasLang) {
        editor = prefs.edit();
        editor.putBoolean("alarm1", hasLang);
        editor.apply();
    }

    public boolean getHasAlarm() {
        return prefs.getBoolean("alarm1", false);
    }


}
