package com.example.bluetoothprinterinvoiceapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothprinterinvoiceapp.R;
import com.example.bluetoothprinterinvoiceapp.models.Invoice;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.OkHttpClient;

public class PreviewReceiptActivity extends AppCompatActivity {

    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;

    InputStream inputStream;
    OutputStream outputStream;
    volatile boolean stopWorker;
    Thread workerThread;
    int readBufferPosition;

    TextView companyInfoView;
    TextView receiptIdView;
    TextView customerNameView;
    TextView addressView;
    TextView customerPrivateValueView;
    TextView customerPrivateTypeView;
    TextView netRegionView;
    TextView receiptValueView;
    TextView representedValueView;
    TextView usernameView;
    TextView dateView;
    TextView writeDetailsView;

    Button printButton;

    Invoice scannedInvoice;

    String username;
    String companyInfo;
    String receiptSeries;
    String receiptNumber;
    String fundedPayment;
    String transmissionKey;
    String serverURL;

    String writeDetails;
    String isCancel;

    OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_receipt);

        // Get Values from Invoices and Settings...
        getInitValues();
        initComponents();
        setValues();

        printButton.setOnClickListener(v -> {
           // Print Button...
        });
    }

    @SuppressLint("SetTextI18n")
    private void setValues() {
        if (!username.equals("") && !companyInfo.equals("") && !receiptSeries.equals("") && !receiptNumber.equals("") && !transmissionKey.equals("") && !serverURL.equals("")){
            companyInfoView.setText(companyInfo);
            receiptIdView.setText(receiptSeries + " / " + receiptNumber);
            customerNameView.setText(scannedInvoice.getInvoiceName());
            addressView.setText(scannedInvoice.getInvoiceAddress1() + " , " + scannedInvoice.getInvoiceAddress2());
            customerPrivateValueView.setText(scannedInvoice.getInvoiceId1());
            receiptValueView.setText(fundedPayment);

            String representedStr;
            if(Double.parseDouble(fundedPayment) < Double.parseDouble(scannedInvoice.getInvoiceValue())){
                representedStr = "C/V factura " + scannedInvoice.getInvoiceNumber() + " din data " + scannedInvoice.getInvoiceDate();
            } else {
                representedStr = "plata partiala factura " + scannedInvoice.getInvoiceNumber() + "din data" + scannedInvoice.getInvoiceDate();
            }
            representedValueView.setText(representedStr);

            usernameView.setText(username);
            dateView.setText("Tiparit la " + getCurrentDate() + " ora " + getCurrentTime());
            writeDetailsView.setText(writeDetails);
        } else {
            Toast.makeText(this, "Please Set the Setting", Toast.LENGTH_LONG).show();
            startActivity(new Intent(PreviewReceiptActivity.this, SettingActivity.class));
            finish();
        }


    }

    private void initComponents() {
        companyInfoView = findViewById(R.id.company_info_view);
        receiptIdView = findViewById(R.id.receipt_id_view);
        customerNameView = findViewById(R.id.customer_name_view);
        addressView = findViewById(R.id.customer_address_view);
        customerPrivateTypeView = findViewById(R.id.customer_private_type_view);
        customerPrivateValueView = findViewById(R.id.customer_private_value_view);
        netRegionView = findViewById(R.id.net_region_view);
        receiptValueView = findViewById(R.id.receipt_value_view);
        representedValueView = findViewById(R.id.receipt_represent_view);
        writeDetailsView = findViewById(R.id.receipt_details_view);
        usernameView = findViewById(R.id.user_name_view);
        dateView = findViewById(R.id.print_date_view);

        printButton = findViewById(R.id.print_button);
    }

    public void getInitValues(){
        // Get Settings Value...
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        username = sharedPreferences.getString(getString(R.string.preference_username_key), "");
        companyInfo = sharedPreferences.getString(getString(R.string.preference_company_key), "");
        receiptSeries = sharedPreferences.getString(getString(R.string.preference_receipt_series_key), "");
        receiptNumber = sharedPreferences.getString(getString(R.string.preference_receipt_number_key), "");
        transmissionKey = sharedPreferences.getString(getString(R.string.preference_transmit_key), "");
        serverURL = sharedPreferences.getString(getString(R.string.preference_server_url_key), "");

        // Get Extra Putted Value...
        scannedInvoice = (Invoice) getIntent().getSerializableExtra("scanned_invoice");
        fundedPayment = getIntent().getStringExtra("funded_payment");
        writeDetails = getIntent().getStringExtra("receipt_details_info");
        isCancel = getIntent().getStringExtra("isCancelable");
    }

    public String getCurrentDate(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return simpleDateFormat.format(new Date());
    }

    public String getCurrentTime(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        Date currentLocalTime = cal.getTime();
        @SuppressLint("SimpleDateFormat") DateFormat date = new SimpleDateFormat("HH:mm:ss");

        date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
        return date.format(currentLocalTime);
    }

}