package br.com.maplus.light;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchListener;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.TouchNetUtil;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.List;

import br.com.maplus.light.Utils.Configuration;
import br.com.maplus.light.Utils.WifiModel;

public class WizardActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST_PERMISSION = 0x01;
    private ViewPager slideViewPager;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private SliderAdapter sliderAdapter;

    private WifiModel wifiModel;

    private int currentPage = 0;
    InetAddress deviceIp = null;

    private IEsptouchListener onEspResult;
    private EsptouchAsyncTask4 mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        wifiModel = new WifiModel();
        slideViewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsSteps);

        sliderAdapter = new SliderAdapter(this, wifiModel);
        slideViewPager.setAdapter(sliderAdapter);
        slideViewPager.addOnPageChangeListener(pageListener);
        addDots(0);

        grantWifiPermission();

        //wifiModel.setBssid(wifiInfo.getBSSID());

    }

    public WifiModel getWifiModel() {
        return wifiModel;
    }

    public void wifiStatus() {
        try {
            WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (manager.isWifiEnabled()) {
                WifiInfo wifiInfo = manager.getConnectionInfo();
                if (wifiInfo != null) {
                    NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                    if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                        String ssid = wifiInfo.getSSID();
                        if (ssid.equalsIgnoreCase("<unknown ssid>")) {
                            Toast.makeText(this, "You are connected to mobile data", Toast.LENGTH_SHORT).show();
                            ssid = "";
                        }
                        wifiModel.setSsid(ssid);
                        wifiModel.setBssid(wifiInfo.getBSSID());

                    } else {
                        Toast.makeText(this, "WIFI not connected", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(this, "No WIFI Information", Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(this, "WIFI not enabled", Toast.LENGTH_SHORT).show();

            }
            TextView wifiName = slideViewPager.findViewWithTag("wizardView").findViewById(R.id.wifiName);
            wifiName.setText(wifiModel.getSsid());
            sliderAdapter.notifyDataSetChanged();
        } catch (Exception xx) {
            Toast.makeText(this, xx.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void grantWifiPermission() {
        try {
            if (ContextCompat.checkSelfPermission(WizardActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                wifiStatus();
            } else {

                ActivityCompat.requestPermissions(WizardActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_PERMISSION);
            }

        } catch (Exception xx) {
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_PERMISSION) {
            Log.d("LIGHTAPP", "REQUESTED RESULT");
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                wifiStatus();
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Log.d("LIGHTAPP", "REQUESTED RESULT not");

                finish();
            }
        }
    }

    protected void SaveConfigurations() {
        Configuration configuration = new Configuration();
        configuration.setAuthToken(wifiModel.getToken());
        configuration.setDeviceIP(deviceIp.getHostAddress());
        configuration.Save(this);
        Intent intent = new Intent(WizardActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void addDots(int position) {
        dots = new TextView[3];
        dotsLayout.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(25, 25, 25, 25);

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(50);
            dots[i].setTextColor(getResources().getColor(R.color.colorText));
            dots[i].setLayoutParams(params);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPage = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void configureEspTouch(String password, String token, IEsptouchListener onEspResult) {
        this.wifiModel.setPassword(password);
        this.wifiModel.setToken(token);
        this.onEspResult = onEspResult;
//        onEspResult.onEsptouchResultAdded(new IEsptouchResult() {
//            @Override
//            public boolean isSuc() {
//              return  true;
//            }
//
//            @Override
//            public String getBssid() {
//                return "99:23:A1:43:34";
//            }
//
//            @Override
//            public boolean isCancelled() {
//                return false;
//            }
//
//            @Override
//            public InetAddress getInetAddress() {
//                try {
//                    return InetAddress.getByName("10.0.0.5");
//                } catch (UnknownHostException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        });
        StartEspTouch();

    }

    public void nextPage() {
        Log.d("LIGHTAPP", "Next page: " + (currentPage + 1));
        slideViewPager.setCurrentItem(currentPage + 1, true);
    }

    void StartEspTouch() {
        byte[] ssid = ByteUtil.getBytesByString(getWifiModel().getSsid()); // Set AP's SSID 78:44:76:18:52:6C  18:0D:2C:6D:F6:49
        byte[] bssid = TouchNetUtil.parseBssid2bytes(getWifiModel().getBssid());
        byte[] password = ByteUtil.getBytesByString(getWifiModel().getPassword()); // Set AP's password
        byte[] deviceCount = "1".getBytes();
        byte[] broadcast = {(byte) (1)};
        if (mTask != null) {
            mTask.cancelEsptouch();
        }
        mTask = new EsptouchAsyncTask4(this, new IEsptouchListener() {
            @Override
            public void onEsptouchResultAdded(IEsptouchResult result) {
                //return success
                Log.d("LIGHTAPP", "IEsptouchResult: " + result.isSuc() + result.getInetAddress() + result.getBssid());
                deviceIp = result.getInetAddress();
                onEspResult.onEsptouchResultAdded(result);
            }
        });
        mTask.execute(ssid, bssid, password, deviceCount, broadcast);

    }

    public void ConfigureIP(boolean DHCP, String IP) {
        Log.d("LIGHTAPP", "IP: " + IP + " DHCP: " + DHCP);
        nextPage();
        //TODO configure IP and finish settings
    }

    private static class EsptouchAsyncTask4 extends AsyncTask<byte[], IEsptouchResult, List<IEsptouchResult>> {
        private WeakReference<WizardActivity> mActivity;

        private final Object mLock = new Object();
        private IEsptouchTask mEsptouchTask;
        private IEsptouchListener esptouchListener;

        EsptouchAsyncTask4(WizardActivity activity, IEsptouchListener listener) {
            this.mActivity = new WeakReference<>(activity);
            this.esptouchListener = listener;
        }

        @Override
        protected void onPreExecute() {
            Activity activity = mActivity.get();
        }

        void cancelEsptouch() {
            cancel(true);

        }

        @Override
        protected void onProgressUpdate(IEsptouchResult... values) {
            Context context = mActivity.get();
            if (context != null) {
                IEsptouchResult result = values[0];
                Log.i("LIGHTAPP", "EspTouchResult: " + result);
                String text = result.getBssid() + " is connected to the wifi";
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected List<IEsptouchResult> doInBackground(byte[]... params) {
            WizardActivity activity = mActivity.get();
            int taskResultCount;
            synchronized (mLock) {
                byte[] apSsid = params[0];
                byte[] apBssid = params[1];
                byte[] apPassword = params[2];
                byte[] deviceCountData = params[3];
                byte[] broadcastData = params[4];
                taskResultCount = deviceCountData.length == 0 ? -1 : Integer.parseInt(new String(deviceCountData));
                Context context = activity.getApplicationContext();
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, context);
                mEsptouchTask.setPackageBroadcast(broadcastData[0] == 1);
                mEsptouchTask.setEsptouchListener(new IEsptouchListener() {
                    @Override
                    public void onEsptouchResultAdded(IEsptouchResult result) {
                        Log.d("LIGHTAPP", " NOV DEVCIE ESP " + result.isSuc() + result.getInetAddress());
                    }
                });
            }
            return mEsptouchTask.executeForResults(taskResultCount);
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            WizardActivity activity = mActivity.get();
            activity.mTask = null;
            if (result == null) {

                return;
            }
            // check whether the task is cancelled and no results received
            IEsptouchResult firstResult = result.get(0);
            if (firstResult.isCancelled()) {
                return;
            }

            esptouchListener.onEsptouchResultAdded(firstResult);
            Log.d("LIGHTAPP", "Result esp: " + firstResult.isSuc() + " IP: " + firstResult.getInetAddress());

        }
    }


}
