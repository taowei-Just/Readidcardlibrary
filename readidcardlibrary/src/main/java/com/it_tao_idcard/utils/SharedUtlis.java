package com.it_tao_idcard.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedUtlis {

    Context context;
    String name;
    private SharedPreferences mPreferences;
    private Editor mEdit;


    public SharedUtlis(Context context, String name) {


        this.context = context;
        this.name = name;
        mPreferences = context.getSharedPreferences(name, Context.MODE_MULTI_PROCESS);
        mEdit = mPreferences.edit();
    }

    public void putString(String key, String value) {


        mEdit.putString(key, value);
        mEdit.commit();

    }
    public void putInt(String key, int value) {


        mEdit.putInt(key, value);
        mEdit.commit();

    }

    public void putBoolean(String key, boolean value) {

        mEdit.putBoolean(key, value);
        mEdit.commit();

    }

    public boolean getBoolean(String key, boolean def) {

        return mPreferences.getBoolean(key, def);


    }

    public String getString(String key, String def) {

        return mPreferences.getString(key, def);


    }

    public int getInt(String key, int def) {

        return mPreferences.getInt(key, def);


    }


}
