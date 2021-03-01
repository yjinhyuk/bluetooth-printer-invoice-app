package com.example.bluetoothprinterinvoiceapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bluetoothprinterinvoiceapp.R;
import com.example.bluetoothprinterinvoiceapp.database.ReceiptDatabase;
import com.example.bluetoothprinterinvoiceapp.database.dao.ReceiptDao;
import com.example.bluetoothprinterinvoiceapp.database.entities.Receipt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    Button scanButton;
    Button transferButton;
    Button settingButton;

    OkHttpClient okHttpClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        scanButton = findViewById(R.id.scan_button);
        transferButton = findViewById(R.id.transfer_button);
        settingButton = findViewById(R.id.setting_button);

        scanButton.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ScanActivity.class));
        });

        settingButton.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SettingActivity.class));
        });

        transferButton.setOnClickListener(v -> {
            // Read all Receipts from Room Database

            if(isNetworkAvailable()){
                ReceiptDatabase receiptDatabase = Room.databaseBuilder(getApplicationContext(), ReceiptDatabase.class, "receipts").build();
                ReceiptDao receiptDao = receiptDatabase.receiptDao();
                List<Receipt> receipts= receiptDao.getAll();
                for (int i = 0; i < receipts.size(); i++) {
                    MediaType MEDIA_TYPE = MediaType.parse("application/json");
                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("NUMAR_FACTURA", receipts.get(i).getNUMAR_FACTURA());
                        postData.put("SERIE_CHITANTA", receipts.get(i).getSERIE_CHITANTA());
                        postData.put("NUMAR_CHITANTA", receipts.get(i).getNUMAR_CHITANTA());
                        postData.put("VALOARE_PLATA", receipts.get(i).getVALOARE_PLATA());
                        postData.put("DATA_PLATA", receipts.get(i).getDATA_PLATA());
                        postData.put("NUME_USER", receipts.get(i).getNUME_USER());
                        postData.put("IS_CANCEL", receipts.get(i).getIS_CANCEL());
                        postData.put("CHEIE_TRANSMISIE", receipts.get(i).getCHEIE_TRANSMISIE());
                        postData.put("COMENTARIU", receipts.get(i).getCOMENTARIU());
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
                    receiptDao.delete(receipts.get(i));
                }
            } else {
                Toast.makeText(getApplicationContext(), "You have no network now", Toast.LENGTH_LONG).show();
            }


        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}