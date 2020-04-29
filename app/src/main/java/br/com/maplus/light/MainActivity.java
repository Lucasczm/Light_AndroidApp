package br.com.maplus.light;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.json.JSONArray;
import org.json.JSONException;

import br.com.maplus.light.Utils.ButtonSwitchListener;
import br.com.maplus.light.Utils.Configuration;
import br.com.maplus.light.Utils.GPIO;
import br.com.maplus.light.Utils.VolleyResponseListener;

public class MainActivity extends AppCompatActivity {

    private VolleySingleton volleySingleton;
    private boolean button1_state = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Configuration configuration = Configuration.getInstance(this);
        Switch useInternetButton = findViewById(R.id.switch_UseInternet);
        final Button lightSwitchButton1 = findViewById(R.id.lightButton1);
        final Button updateButton = findViewById(R.id.updateButton);

        volleySingleton = VolleySingleton.getInstance(this);

        ReadGPIOS(GPIO.GPIO_V0, lightSwitchButton1, new ButtonSwitchListener() {
            @Override
            public void onSuccess(boolean state) {
                button1_state = state;
            }
        });

        useInternetButton.setChecked(configuration.isUseInternet());
        useInternetButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                configuration.setUseInternet(b);
                configuration.Save(compoundButton.getContext());
            }
        });

        lightSwitchButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LightSwitchGPIO(GPIO.GPIO_V0, button1_state, new ButtonSwitchListener() {
                    @Override
                    public void onSuccess(boolean state) {
                        button1_state = !button1_state;
                        ChangeStateButton(lightSwitchButton1, button1_state);
                    }
                });
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReadGPIOS(GPIO.GPIO_V0, lightSwitchButton1, new ButtonSwitchListener() {
                    @Override
                    public void onSuccess(boolean state) {
                        button1_state = state;
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuSettings) {
            OpenSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void OpenSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void LightSwitchGPIO(GPIO gpio, final boolean actual_state, final ButtonSwitchListener buttonSwitchListener) {
        volleySingleton.gpioWrite(gpio, (actual_state) ? GPIO.DIGITAL.LOW : GPIO.DIGITAL.HIGHT, new VolleyResponseListener<String>() {
            @Override
            public void onError(String message) {
                Log.e("LIGHTAPP", message);
            }

            @Override
            public void onResponse(String response) {
                buttonSwitchListener.onSuccess(actual_state);
                Log.d("LIGHTAPP", response.toString());
            }
        });

    }

    private void ChangeStateButton(Button btn, boolean state) {
        Drawable buttonDrawable = btn.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        //the color is a direct color int and not a color resource
        int color;
        if (state)
            color = getResources().getColor(R.color.colorAccent);
        else
            color = getResources().getColor(R.color.colorDisable);
        DrawableCompat.setTint(buttonDrawable, color);
        btn.setBackground(buttonDrawable);
    }

    private void ReadGPIOS(GPIO gpio, final Button button, final ButtonSwitchListener buttonSwitchListener) {
        volleySingleton.gpioRead(gpio, new VolleyResponseListener<JSONArray>() {
            @Override
            public void onError(String message) {
                Log.e("LIGHTAPP", message);
            }

            @Override
            public void onResponse(JSONArray response) {
                Boolean value;
                try {
                    value = response.getString(0).equals("1");
                    Log.d("LIGHTAPP", "Valor: " + value);
                    ChangeStateButton(button, value);
                    buttonSwitchListener.onSuccess(value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
