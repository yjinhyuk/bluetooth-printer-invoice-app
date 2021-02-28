package com.example.bluetoothprinterinvoiceapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothprinterinvoiceapp.R;
import com.example.bluetoothprinterinvoiceapp.models.Invoice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.intandif.viewtoimageorpdf.ActionListeners;
import com.intandif.viewtoimageorpdf.ViewToImage;

import org.json.JSONException;
import org.json.JSONObject;

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
            LinearLayout printLayout = findViewById(R.id.print_layout);
            printDataViaBluetoothPrinter(printLayout);
            transferDataToServer();
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void transferDataToServer() {
        if(isNetworkAvailable()){
            MediaType MEDIA_TYPE = MediaType.parse("application/json");
            JSONObject postData = new JSONObject();
            try {
                postData.put("NUMAR_FACTURA", scannedInvoice.getInvoiceNumber());
                postData.put("SERIE_CHITANTA", receiptSeries);
                postData.put("NUMAR_CHITANTA", receiptNumber);
                postData.put("VALOARE_PLATA", Double.toString(Double.parseDouble(fundedPayment)*100));
                postData.put("DATA_PLATA", getCurrentDate());
                postData.put("NUME_USER", scannedInvoice.getInvoiceName());
                postData.put("IS_CANCEL", isCancel);
                postData.put("CHEIE_TRANSMISIE", transmissionKey);
                postData.put("COMENTARIU", writeDetails);
            } catch(JSONException e){
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(MEDIA_TYPE, postData.toString());

            Request request = new Request.Builder()
                    .url("http://ecare.bizarnet.ro/staging.asp")
                    .post(body)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .build();
            okHttpClient = new OkHttpClient();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    String mMessage = e.getMessage().toString();
                    Log.w("failure Response", mMessage);
                    //call.cancel();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String mMessage = response.body().string();
                    Log.e("success Response", mMessage);
                }
            });
        } else {
            // Store Data to Local Database //
            // Create Database in local //

        }
    }



    private void printDataViaBluetoothPrinter(LinearLayout printLayout) {
        boolean isPermitted = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                isPermitted= true;
            }
        }

        if(!isPermitted){
            Toast.makeText(PreviewReceiptActivity.this, "No Permission Granted" , Toast.LENGTH_SHORT).show();
            return;
        }

        new ViewToImage(PreviewReceiptActivity.this, printLayout, new ActionListeners() {
            @Override
            public void convertedWithSuccess(Bitmap bitmap, String filePath) {
                Toast.makeText(PreviewReceiptActivity.this, "" + filePath, Toast.LENGTH_SHORT).show();
                //ToDo  //With File Path you can Do Whatever You want
                //ToDo  //Use Bitmap also

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                try {
                    findBlueToothDevice();
                    openBlueToothDevice();
                    sendData(byteArrayOutputStream.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            if(!bluetoothAdapter.isEnabled()){
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

    public void sendData(byte[] bytes) throws IOException{
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
            workerThread = new Thread(new Runnable() {
                public void run() {
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
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
            workerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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