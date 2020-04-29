package br.com.maplus.light;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

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

    private String makeURL() {
        return configuration.isUseInternet() ? " http://blynk-cloud.com/" + configuration.getAuthToken() : "http://" + configuration.getDeviceIP() + "/" + configuration.getAuthToken();
    }

    private String makeGPIOWriteRequest(GPIO gpio, GPIO.DIGITAL value) {
        return makeURL() + "/update/" + gpio.getText() + "?value=" + value.ordinal();
    }

    private String makeGPIOReadRequest(GPIO gpio) {
        return makeURL() + "/get/"+ gpio.getText();
    }

    void gpioWrite(GPIO gpio, GPIO.DIGITAL value, final VolleyResponseListener listener) {
        StringRequest stringRequest = new StringRequest(makeGPIOWriteRequest(gpio, value), new Response.Listener<String>() {
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
        requestQueue.add(stringRequest);
    }

    void gpioRead(GPIO gpio, final VolleyResponseListener listener){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(makeGPIOReadRequest(gpio), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                listener.onResponse(response);
            }

        },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
}
