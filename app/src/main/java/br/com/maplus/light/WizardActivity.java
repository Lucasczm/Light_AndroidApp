package br.com.maplus.light;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.maplus.light.Utils.Configuration;

public class WizardActivity extends AppCompatActivity {

    private EditText authTokenInput, ipDeviceInput;
    private ViewPager slideViewPager;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private SliderAdapter sliderAdapter;

    private int currentPage = 0;
    String wifiPassword = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        slideViewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsSteps);

        sliderAdapter = new SliderAdapter(this);
        slideViewPager.setAdapter(sliderAdapter);
        slideViewPager.addOnPageChangeListener(pageListener);
        addDots(0);
        //   authTokenInput = findViewById(R.id.deviceCodeEditText_wizard);
        //    ipDeviceInput = findViewById(R.id.ipEditText_wizard);
//        Button saveButton = findViewById(R.id.continueButton);

//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SaveConfigurations();
//            }
//        });
    }

//    protected void SaveConfigurations() {
//        Configuration configuration = new Configuration();
//        configuration.setAuthToken(authTokenInput.getText().toString());
//        configuration.setDeviceIP(ipDeviceInput.getText().toString());
//        configuration.Save(this);
//        Intent intent = new Intent(WizardActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
//    }

    private void addDots(int position) {
        dots = new TextView[3];
        dotsLayout.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(25, 25, 25, 50);

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

    public void configureEspNow(String password, String token) {
        this.wifiPassword = password;
        //Configure espnow
    }

    public void nextPage() {
        Log.d("LIGHTAPP", "Next page: " + currentPage + 1);
        slideViewPager.setCurrentItem(currentPage + 1);
    }
}
