package br.com.maplus.light.Utils;

public interface VolleyResponseListener {
    void onError(String message);

    void onResponse(Object response);
}