package com.alburagh.alburagh.Helpers;

import static com.alburagh.alburagh.Helpers.AppHelper.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Setting {
    private static Setting instance = null;
    private Context mContext;

    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;

    private static final String PREFERENCE_TITLE = "alburagh_env";

    private Setting(){}

    public static Setting getInstance(){
        if(instance == null)
            instance = new Setting();
        return instance;
    }

    public void setContext (Context context) {
        mContext = context;
        preferences = context.getSharedPreferences(PREFERENCE_TITLE, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setVariable (String key, String value) {
        editor.putString(key, value).commit();
        editor.commit();
    }

    public String getVariable (String key) {
        Log.i(TAG, "getVariableKey1: "+key);
        return preferences.getString(key,"");
    }

    public void removeVariable (String key) {
        if (preferences.contains(key)) {
            editor.remove(key);
            editor.commit();
        }
    }

}
