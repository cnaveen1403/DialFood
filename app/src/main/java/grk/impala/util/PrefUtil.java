package grk.impala.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Samsung on 6/11/2015.
 */
public class PrefUtil {

    public static final String TOTAL_QTY = "totalQty";
    public static final String SET_ORDER = "set_order";
    public static final String LOGGED_IN = "logged_in";
    public static final String SET_EMAIL_ID = "set_email_id";
    public static final String USER_TYPE = "set_user_type";
    public static final String SET_MOBILE = "set_mobile";
    public static final String SET_CITY = "set_city";
    public static final String SET_ADDRESS = "set_address";
    public static final String SET_NAME = "set_name";
    public static final String DEVICE_ID = "set_device_id";

    public static void setTotalQty(Context context, int qty){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(TOTAL_QTY, qty).commit();
    }

    public static int getTotalQty(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(TOTAL_QTY, 0);
    }

    public static void setSetOrder(Context context, boolean val){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(SET_ORDER, val).commit();
    }

    public static boolean getSetOrder(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(SET_ORDER, false);
    }

    public static void setLoggedIn(Context context, boolean val) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(LOGGED_IN, val).commit();
    }

    public static boolean getLoggedIn(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(LOGGED_IN, false);
    }

    public static void setEmailId(Context context, String val) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(SET_EMAIL_ID, val).commit();
    }

    public static String getEmailId(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(SET_EMAIL_ID, "");
    }

    public static void setMobile(Context context, String val) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(SET_MOBILE, val).commit();
    }

    public static String getMobile(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(SET_MOBILE, "");
    }

    public static void setAddress(Context context, String val) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(SET_ADDRESS, val).commit();
    }

    public static String getAddress(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(SET_ADDRESS, "");
    }

    public static void setCity(Context context, String val) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(SET_CITY, val).commit();
    }

    public static String getCity(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(SET_CITY, "");
    }

    public static void setName(Context context, String val) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(SET_NAME, val).commit();
    }

    public static String getName(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(SET_NAME, "");
    }

    public static void setUserType(Context context, int val){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(USER_TYPE, val).commit();
    }

    public static int getUserType(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(USER_TYPE, 0);
    }

    public static void setDeviceId(Context context, String val) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(DEVICE_ID, val).commit();
    }

    public static String getDeviceId(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(DEVICE_ID, "");
    }

    public static void setClearCache(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().clear().commit();
    }
}
