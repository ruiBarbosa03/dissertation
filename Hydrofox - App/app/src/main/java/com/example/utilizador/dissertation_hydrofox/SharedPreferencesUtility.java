package com.example.utilizador.dissertation_hydrofox;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by utilizador on 25/04/2017.
 */

public class SharedPreferencesUtility {

    static SharedPreferences pref;
    static SharedPreferences.Editor editor;

    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "My_Prefs";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_LOGIN= "name";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_DEVICEID = "device_id";
    public static final String KEY_PHONENUMBER = "phonenumber";
    public static final String KEY_FIRSTREADING = "first_reading";
    public static final String KEY_COST = "cost";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_FUGAS_ON_OFF = "fugasSwitch";
    public static final String KEY_FUGAS_LITERS = "fugasLiters";
    public static final String KEY_MODO = "modo";


    // Constructor
    public SharedPreferencesUtility(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public static void makeLogin(String login, String password){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing name in pref
        editor.putString(KEY_LOGIN, login);
        editor.putString(KEY_PASSWORD, password);
        // commit changes
        editor.commit();
    }

    public void setUsername(String username){
        editor.putString(KEY_USERNAME, username);
        editor.commit();
    }
    public void setDeviceID(String deviceID){
        editor.putString(KEY_DEVICEID, deviceID);
        editor.commit();
    }
    public void setPhonenumber(String phonenumber){
        editor.putString(KEY_PHONENUMBER, phonenumber);
        editor.commit();
    }
    public void setCost(Float cost){
        editor.putFloat(KEY_COST, cost);
        editor.commit();
    }
    public void setFirstreading(Integer firstread){
        editor.putInt(KEY_FIRSTREADING, firstread);
        editor.commit();
    }
    public void setFugas(boolean fugas){
        editor.putBoolean(KEY_FUGAS_ON_OFF, fugas);
        editor.commit();
    }
    public void setFugasLiters(int fugas){
        editor.putInt(KEY_FUGAS_LITERS, fugas);
        editor.commit();
    }
    public void setModo(boolean modo){
        editor.putBoolean(KEY_MODO, modo);
        editor.commit();
    }

    public String getUsername(){
        return pref.getString(KEY_USERNAME, null);
    }
    public static String getPassword(){
        return pref.getString(KEY_PASSWORD, null);
    }
    public static String getLogin(){
        return pref.getString(KEY_LOGIN, null);
    }
    public static String getDeviceID(){ return pref.getString(KEY_DEVICEID, null);}
    public static Boolean getFugas(){ return pref.getBoolean(KEY_FUGAS_ON_OFF, false);}
    public static Integer getFugasLiters(){ return pref.getInt(KEY_FUGAS_LITERS, 0);}
    public static Boolean getModo(){ return pref.getBoolean(KEY_MODO, false);}

    public static void deleteAll(){ editor.clear();}
}
