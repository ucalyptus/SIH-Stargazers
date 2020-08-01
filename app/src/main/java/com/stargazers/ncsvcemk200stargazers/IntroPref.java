package com.stargazers.ncsvcemk200stargazers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class IntroPref {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "com.stargazers.awaas.users";
    private static final String IS_FIRST_TIME_LAUNCH = "firstTime";
    private static final String ACCOUNT_TYPE = "accountType"; // 0 = officer, 1 = benificiary


    @SuppressLint("CommitPrefEdits")
    public IntroPref(Context context){
        this.context = context;
        if(context != null) {
            preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        }
        editor = preferences.edit();
    }

    public void setIsFirstTimeLaunch(boolean firstTimeLaunch){
        editor.putBoolean(IS_FIRST_TIME_LAUNCH,firstTimeLaunch);
        editor.commit();
    }

    public boolean isFirstTimeLaunch(){
        return preferences.getBoolean(IS_FIRST_TIME_LAUNCH,true);
    }

    public void setAccountType(int accountType){
        editor.putInt(ACCOUNT_TYPE, accountType);
        editor.commit();
    }

    public int getAccountType(){
        return preferences.getInt(ACCOUNT_TYPE,0);
    }

}