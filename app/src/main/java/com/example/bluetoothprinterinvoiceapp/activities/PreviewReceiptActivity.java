package com.example.bluetoothprinterinvoiceapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothprinterinvoiceapp.R;
import com.example.bluetoothprinterinvoiceapp.database.ReceiptDatabase;
import com.example.bluetoothprinterinvoiceapp.database.dao.ReceiptDao;
import com.example.bluetoothprinterinvoiceapp.database.entities.Receipt;
import com.example.bluetoothprinterinvoiceapp.models.Invoice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;


import com.intandif.viewtoimageorpdf.ActionListeners;
import com.intandif.viewtoimageorpdf.ViewToImage;


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
    TextView receiptValueView;
    TextView receiptPrivateInfoView;
    TextView dateView;
    TextView writeDetailsView;
    TextView usernameView;

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
            View printView = findViewById(R.id.print_layout);
            printDataViaBluetoothPrinter(printView);
            try {
                transferDataToServer();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void transferDataToServer() throws IOException {
        if(isNetworkAvailable()){

//            okHttpClient = new OkHttpClient();
//            Log.e("iscancel", isCancel);
//
//            RequestBody formBody = new FormBody.Builder()
//                    .add("NUMAR_FACTURA", scannedInvoice.getInvoiceNumber())
//                    .add("SERIE_CHITANTA", receiptSeries)
//                    .add("NUMAR_CHITANTA", receiptNumber)
//                    .add("VALOARE_PLATA", Double.toString(Double.parseDouble(fundedPayment)*100))
//                    .add("DATA_PLATA", getCurrentDate())
//                    .add("NUME_USER", scannedInvoice.getInvoiceName())
//                    .add("IS_CANCEL", isCancel)
//                    .add("CHEIE_TRANSMISIE", transmissionKey)
//                    .add("COMENTARIU", writeDetails)
//                    .build();
//            Request request = new Request.Builder().url("http://ecare.bizarnet.ro/staging.asp").post(formBody).build();
//            Response response = okHttpClient.newCall(request).execute();
//            if (!response.isSuccessful())
//                throw new IOException("Unexpected code " + response);
//            System.out.println(response.body().string());


        } else {
            // Store Data to Local Database //
            // Create Database in local //
            ReceiptDatabase receiptDatabase = Room.databaseBuilder(getApplicationContext(), ReceiptDatabase.class, "receipts").build();
            ReceiptDao receiptDao = receiptDatabase.receiptDao();

            Receipt receipt = new Receipt();
            receipt.setNUMAR_FACTURA(scannedInvoice.getInvoiceNumber());
            receipt.setSERIE_CHITANTA(receiptSeries);
            receipt.setNUMAR_CHITANTA(receiptNumber);
            receipt.setVALOARE_PLATA(fundedPayment);
            receipt.setDATA_PLATA(getCurrentDate());
            receipt.setNUME_USER(username);
            receipt.setCHEIE_TRANSMISIE(transmissionKey);
            receipt.setIS_CANCEL(isCancel);
            receipt.setCOMENTARIU(writeDetails);

            receiptDao.insert(receipt);
        }
    }



    private void printDataViaBluetoothPrinter(View printView) {

        new ViewToImage(PreviewReceiptActivity.this, printView, new ActionListeners() {
            @Override
            public void convertedWithSuccess(Bitmap bitmap, String filePath) {
                Toast.makeText(PreviewReceiptActivity.this, "" + filePath, Toast.LENGTH_SHORT).show();
                //ToDo  //With File Path you can Do Whatever You want
                //ToDo  //Use Bitmap also

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                findBlueToothDevice();
                openBlueToothDevice();
                sendData(byteArrayOutputStream.toByteArray());
                closeBlueToothDevice();
            }
            @Override
            public void convertedWithError(String error) {
                Toast.makeText(PreviewReceiptActivity.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findBlueToothDevice(){
        try{
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(), "No Bluetooth Adapter Available", Toast.LENGTH_LONG).show();
            }
            if(!Objects.requireNonNull(bluetoothAdapter).isEnabled()){
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device:pairedDevices){
                    if(device.getName().equals("RPP200")){
                        bluetoothDevice = device;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeBlueToothDevice() {
        try{
            stopWorker = true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
            Toast.makeText(getApplicationContext(), "Bluetooth Closed", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openBlueToothDevice(){
        try{
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();

            beginListenForData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendData(byte[] bytes) {
        Log.d("aaa", "bbb");
        try{
            outputStream.write(bytes);
            Toast.makeText(getApplicationContext(), "Data Sent", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void beginListenForData() {
        try {
            final Handler handler = new Handler();
            // this is the ASCII code for a newline character
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            byte[] readBuffer = new byte[1024];
            workerThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = inputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(
                                            readBuffer, 0,
                                            encodedBytes, 0,
                                            encodedBytes.length
                                    );
                                    // specify US-ASCII encoding
                                    String data = "";
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                        data = new String(encodedBytes, StandardCharsets.US_ASCII);
                                    }
                                    readBufferPosition = 0;

                                    /* tell the user data were sent to bluetooth printer device */
                                    String finalData = data;
                                    handler.post(() -> Toast.makeText(getApplicationContext(), finalData, Toast.LENGTH_LONG).show());

                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            });
            workerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void initComponents() {
        companyInfoView = findViewById(R.id.company_info_view);
        receiptIdView = findViewById(R.id.receipt_id_view);
        customerNameView = findViewById(R.id.customer_name_view);
        addressView = findViewById(R.id.customer_address_view);
        receiptPrivateInfoView = findViewById(R.id.receipt_private_info_view);
        receiptValueView = findViewById(R.id.receipt_value_view);
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
        isCancel = getIntent().getStringExtra("isCancel");
    }

    @SuppressLint("SetTextI18n")
    private void setValues() {
        if (!username.equals("") && !companyInfo.equals("") && !receiptSeries.equals("") && !receiptNumber.equals("") && !transmissionKey.equals("") && !serverURL.equals("")){

            companyInfoView.setText(companyInfo);
            receiptIdView.setText("CHITANTA" + receiptSeries + " " + receiptNumber + " / " + getCurrentDate());

            String customerNameStr = "Am primit de la " + "<b>"+scannedInvoice.getInvoiceName()+"</b>";
            customerNameView.setText(Html.fromHtml(customerNameStr));

            String addressStr = "Adresa: "+"<b>"+scannedInvoice.getInvoiceAddress1()+ ", "+scannedInvoice.getInvoiceAddress2();
            addressView.setText(Html.fromHtml(addressStr));

            String receiptPrivateStr = "CIF: " + "<b>" + scannedInvoice.getInvoiceId2()+"</b>";
            receiptPrivateInfoView.setText(Html.fromHtml(receiptPrivateStr));



            String representedStr;
            if(Double.parseDouble(fundedPayment) > Double.parseDouble(scannedInvoice.getInvoiceValue())){
                representedStr = "C/V factura " + scannedInvoice.getInvoiceNumber() + " / " + scannedInvoice.getInvoiceDate();
            } else {
                representedStr = "plata partiala factura " + scannedInvoice.getInvoiceNumber() + " / " + scannedInvoice.getInvoiceDate();
            }
            String receiptValueStr = "suma de "+"<b>"+fundedPayment+"</b>"+" lei, reprezentand "+"<b>"+representedStr+"</b>";
            receiptValueView.setText(Html.fromHtml(receiptValueStr));

            usernameView.setText(username);
            dateView.setText("Tiparit la " + getCurrentDate() + " ora " + getCurrentTime());
            writeDetailsView.setText(writeDetails);
        } else {
            Toast.makeText(this, "Please Set the Setting", Toast.LENGTH_LONG).show();
            startActivity(new Intent(PreviewReceiptActivity.this, SettingActivity.class));
            finish();
        }
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