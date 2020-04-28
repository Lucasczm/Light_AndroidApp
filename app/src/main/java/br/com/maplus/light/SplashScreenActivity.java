package br.com.maplus.light;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import br.com.maplus.light.Utils.Configuration;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override public void run() {
                StartApp();
            }
        }, 2000);
    }
    private void StartApp() {
        Configuration configuration = Configuration.getInstance(this);
        Intent intent;
        if(configuration == null) {
            intent = new Intent(SplashScreenActivity.this, WizardActivity.class);
        } else{
            intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
