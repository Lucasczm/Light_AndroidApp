package br.com.maplus.light.Utils;

import org.json.JSONException;

public interface VolleyResponseListener<T> {
    void onError(String message);

    void onResponse(T response);
}