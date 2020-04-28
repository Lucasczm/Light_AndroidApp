package br.com.maplus.light;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import br.com.maplus.light.Utils.Configuration;
import br.com.maplus.light.Utils.GPIO;
import br.com.maplus.light.Utils.VolleyResponseListener;

class VolleySingleton {
    private static VolleySingleton instance;
    private RequestQueue requestQueue;
    private Configuration configuration;

    private VolleySingleton(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        configuration = Configuration.getInstance(context.getApplicationContext());
    }

    static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    private String makeGPIORequest(GPIO gpio, GPIO.DIGITAL value) {
        String blynkURL = "http://blynk-cloud.com/";
        if (configuration.isUseInternet()) {
            return blynkURL + configuration.getAuthToken() + "/update/" + gpio.getText() + "?value=" + value.ordinal();
        } else {
            return "http://" + configuration.getDeviceIP() + "/" + configuration.getAuthToken() + "/update/" + gpio.getText() + "?value=" + value.ordinal();
        }
    }

    void gpioWrite(GPIO gpio, GPIO.DIGITAL value, final VolleyResponseListener listener) {
        StringRequest jsonObjectRequest = new StringRequest(makeGPIORequest(gpio, value), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
