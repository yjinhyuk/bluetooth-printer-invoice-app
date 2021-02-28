package com.example.bluetoothprinterinvoiceapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bluetoothprinterinvoiceapp.R;

public class HomeActivity extends AppCompatActivity {
    Button scanButton;
    Button transferButton;
    Button settingButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        scanButton = findViewById(R.id.scan_button);
        transferButton = findViewById(R.id.transfer_button);
        settingButton = findViewById(R.id.setting_button);

        scanButton.setOnClickListener(v -> {
//            startActivity(new Intent(HomeActivity.this, ScanActivity.class));
        });

        settingButton.setOnClickListener(v -> {
//            startActivity(new Intent(HomeActivity.this, SettingActivity.class));
        });

        transferButton.setOnClickListener(v -> {

        });
    }
}