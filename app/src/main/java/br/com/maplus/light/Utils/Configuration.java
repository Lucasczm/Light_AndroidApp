package br.com.maplus.light.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class Configuration {
    private static final String storageTag = "configuration";
    private static Configuration instance;
    private String authToken;
    private String deviceIP;
    private boolean useInternet;

    public static synchronized Configuration getInstance(Context context) {
        if (instance == null) {
            instance = Configuration.LoadConfigurations(context);
        }
        return instance;
    }

    public String getAuthToken() {
        return this.authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getDeviceIP() {
        return this.deviceIP;
    }

    public void setDeviceIP(String deviceIP) {
        this.deviceIP = deviceIP;
    }

    public boolean isUseInternet() {
        return this.useInternet;
    }

    public void setUseInternet(boolean useInternet) {
        Configuration.instance.useInternet = useInternet;
    }

    public boolean Save(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(this);
        prefsEditor.putString(Configuration.storageTag, json);
        return prefsEditor.commit();
    }

    private static Configuration LoadConfigurations(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(Configuration.storageTag, "");
        return gson.fromJson(json, Configuration.class);
    }
}
