package com.example.bluetoothprinterinvoiceapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.bluetoothprinterinvoiceapp.R;

public class SettingActivity extends AppCompatActivity {

    EditText usernameEdit;
    EditText companyEdit;
    EditText receiptSeriesEdit;
    EditText receiptNumberEdit;
    EditText transmitEdit;
    EditText serverURLEdit;

    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        usernameEdit = findViewById(R.id.user_name_edit);
        companyEdit = findViewById(R.id.company_info_edit);
        receiptSeriesEdit = findViewById(R.id.receipt_series_edit);
        receiptNumberEdit = findViewById(R.id.receipt_number_edit);
        transmitEdit = findViewById(R.id.transmission_key_edit);
        serverURLEdit = findViewById(R.id.server_url_edit);
        saveButton = findViewById(R.id.save_button);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String username = sharedPreferences.getString(getString(R.string.preference_username_key), "");
        String companyInfo = sharedPreferences.getString(getString(R.string.preference_company_key), "");
        String receiptSeries = sharedPreferences.getString(getString(R.string.preference_receipt_series_key), "");
        String receiptNumber = sharedPreferences.getString(getString(R.string.preference_receipt_number_key), "");
        String transmit_key = sharedPreferences.getString(getString(R.string.preference_transmit_key), "");
        String server_url = sharedPreferences.getString(getString(R.string.preference_server_url_key), "");

        usernameEdit.setText(username);
        companyEdit.setText(companyInfo);
        receiptSeriesEdit.setText(receiptSeries);
        receiptNumberEdit.setText(receiptNumber);
        transmitEdit.setText(transmit_key);
        serverURLEdit.setText(server_url);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v) {
                // Save the settings globally...
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(getString(R.string.preference_username_key), usernameEdit.getText().toString());
                editor.putString(getString(R.string.preference_company_key), companyEdit.getText().toString());
                editor.putString(getString(R.string.preference_receipt_series_key), receiptSeriesEdit.getText().toString());
                editor.putString(getString(R.string.preference_receipt_number_key), receiptNumberEdit.getText().toString());
                editor.putString(getString(R.string.preference_transmit_key), transmitEdit.getText().toString());
                editor.putString(getString(R.string.preference_server_url_key), serverURLEdit.getText().toString());

                editor.apply();
                // Finish Activity
                finish();
            }
        });

    }
}