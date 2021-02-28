package com.example.bluetoothprinterinvoiceapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothprinterinvoiceapp.R;
import com.example.bluetoothprinterinvoiceapp.models.Invoice;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends AppCompatActivity {

    CameraSource cameraSource;

    SurfaceView surfaceView;
    TextView qrStatusView;

    private static final int REQUEST_CAMERA_PERMISSION = 201;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        surfaceView = findViewById(R.id.surface_view);
        qrStatusView = findViewById(R.id.qr_status_view);
    }

    @Override
    protected void onPause(){
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume(){
        super.onResume();
        initialiseDetectorsAndSources();
    }

    private void initialiseDetectorsAndSources() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(1024, 768).setAutoFocusEnabled(true).build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                try{
                    if (ActivityCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScanActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(),"To Prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodeSparseArray = detections.getDetectedItems();
                if(barcodeSparseArray.size() != 0) {
                    qrStatusView.post(() -> {
                        // Read QR Code By String...
                        String qrCodeString = barcodeSparseArray.valueAt(0).displayValue;
                        Toast.makeText(getApplicationContext(), "QR Code Scanned", Toast.LENGTH_SHORT).show();

                        // Converting QR code String into Invoice class model...
                        try{
                            JSONObject qrObj = new JSONObject(qrCodeString);
                            Invoice scannedInvoice = new Invoice();
                            scannedInvoice.setInvoiceName(qrObj.getString("NUME"));
                            scannedInvoice.setInvoiceAddress1(qrObj.getString("ADRESA1"));
                            scannedInvoice.setInvoiceAddress2(qrObj.getString("ADRESA2"));
                            scannedInvoice.setInvoiceId1(qrObj.getString("ID1"));
                            scannedInvoice.setInvoiceId2(qrObj.getString("ID2"));
                            scannedInvoice.setInvoiceNumber(qrObj.getString("NUMAR_FACTURA"));
                            scannedInvoice.setInvoiceDate(qrObj.getString("DATA_FACTURA"));
                            scannedInvoice.setInvoiceValue(qrObj.getString("VALOARE_FACTURA"));

                            JSONArray numArray = qrObj.getJSONArray("TELEFON");
                            List<String> phoneNumbers = new ArrayList<>();
                            for(int i = 0; i < numArray.length(); i++){
                                JSONObject phoneJsonObj = numArray.getJSONObject(i);
                                String phoneNum = phoneJsonObj.getString("NUMAR");
                                phoneNumbers.add(phoneNum);
                            }
                            scannedInvoice.setInvoicePhoneNumbers(phoneNumbers);

                            Intent fundIntent = new Intent(ScanActivity.this, FundActivity.class);
                            fundIntent.putExtra("scanned_invoice", scannedInvoice);
                            startActivity(fundIntent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }
}