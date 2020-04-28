package br.com.maplus.light;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.com.maplus.light.Utils.Configuration;

public class WizardActivity extends AppCompatActivity {

    private EditText authTokenInput, ipDeviceInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);
        
        authTokenInput = findViewById(R.id.deviceCodeEditText_wizard);
        ipDeviceInput = findViewById(R.id.ipEditText_wizard);
        Button saveButton = findViewById(R.id.continueButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveConfigurations();
            }
        });
    }
    
    protected void SaveConfigurations(){
        Configuration configuration = new Configuration();
        configuration.setAuthToken(authTokenInput.getText().toString());
        configuration.setDeviceIP(ipDeviceInput.getText().toString());
        configuration.Save(this);
        Intent intent = new Intent(WizardActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
