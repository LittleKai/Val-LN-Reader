package com.valvrare.littlekai.valvraretranslation.database;

/**
 * Created by Kai on 12/1/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class Var {


    // const for load and put data with server
    public static final String KEY_METHOD = "method";

    public static final String TAG = "Kai";

    public static final String KEY_NICK = "nick";
    public static final String KEY_PASS = "pass";

    public static final String KEY_ID = "id";
    public static final String KEY_NOVEL_NAME = "novel_name";
    public static final String KEY_NAME = "chapter_name";
    public static final String KEY_URL = "url";
    public static final String KEY_PUBLISH_DATE = "publish_date";
    public static final String KEY_INPUT_PRICE = "inputprice";
    public static final String KEY_PRICE = "price";
    public static final String KEY_REMAIN = "remain";
    public static final String KEY_IMAGE = "image";

    public static final String KEY_LOGIN = "login";
    public static final String KEY_REGISTER = "register";
    public static final String KEY_ADD = "add";

    public static final int METHOD_GET_ALL_TYPES = 1;
    public static final int METHOD_GET_ALL_PRODUCTS = 2;

    public static final int METHOD_GET_OLD_LOVER = 3;
    public static final int METHOD_ADD_PRODUCT = 4;
    public static final int METHOD_ADD_IMAGE = 5;
    public static final int METHOD_DELETE_ALL_PRODUCT = 10;
    public static final int METHOD_DELETE_ALL_TYPE = 11;

    public static void showToast(Context context, String sms) {
        Toast.makeText(context, sms, Toast.LENGTH_SHORT).show();
    }

    // method for save and get nick and pass user

    public static void save(Context context, String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext())
                .edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String get(Context context, String key) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        return settings.getString(key, null);
    }
}
