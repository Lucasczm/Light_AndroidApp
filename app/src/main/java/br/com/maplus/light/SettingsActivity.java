package br.com.maplus.light;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.com.maplus.light.Utils.Configuration;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final Configuration configuration = Configuration.getInstance(this);

        final EditText authTokenInput = findViewById(R.id.deviceCodeEditText);
        final EditText ipDeviceInput = findViewById(R.id.ipEditText);
        Button backButton = findViewById(R.id.backButton);
        Button saveButton = findViewById(R.id.saveButton);

        authTokenInput.setText(configuration.getAuthToken());
        ipDeviceInput.setText(configuration.getDeviceIP());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configuration.setAuthToken(authTokenInput.getText().toString());
                configuration.setDeviceIP(ipDeviceInput.getText().toString());
                configuration.Save(view.getContext());
                finish();
            }
        });

    }
}
