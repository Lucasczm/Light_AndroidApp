package br.com.maplus.light;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.gson.Gson;


public class Utils {

    public static Configuration LoadConfigurations(Context context){
        SharedPreferences mPrefs = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("configuration", "");
        Configuration config = gson.fromJson(json, Configuration.class);
        return  config;
    }

    public static boolean SaveConfiguration(Configuration configuration, Context context){
        SharedPreferences mPrefs = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(configuration);
        prefsEditor.putString("configuration", json);
        return prefsEditor.commit();
    }

}
