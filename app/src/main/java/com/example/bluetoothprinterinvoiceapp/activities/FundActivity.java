package com.example.bluetoothprinterinvoiceapp.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bluetoothprinterinvoiceapp.R;
import com.example.bluetoothprinterinvoiceapp.adapters.PhoneNumberListAdapter;
import com.example.bluetoothprinterinvoiceapp.models.Invoice;

public class FundActivity extends AppCompatActivity {

    Invoice scannedInvoice;

    TextView invoiceNameView;
    TextView invoiceNumberView;
    TextView invoiceAddressView;
    TextView invoiceValueView;
    TextView invoiceIDView1;
    TextView invoiceIDView2;
    TextView invoiceDateView;

    LinearLayout fundedPaymentLayout;
    TextView fundedPaymentView;
    TextView payBackAmountView;

    LinearLayout writeDetailsLayout;
    EditText writeDetailsView;

    Button callPhoneButton;
    Button checkoutButton;
    Button fundPaymentButton;

    String isCancel;
    String fundedAmount;
    String writeDetailsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund);

        // Get Invoice from ScanActivity...
        scannedInvoice = (Invoice) getIntent().getSerializableExtra("scanned_invoice");

        // Init Views...
        initViews();
        // Set Views Value
        setValues();
        // Set Actions...
        callPhoneButton.setOnClickListener(v -> {
            showCallListDialog();
        });
        fundPaymentButton.setOnClickListener(v -> {
            showPayAmountDialog();
        });
        checkoutButton.setOnClickListener(v -> {
            showCancelDialog();
        });


    }

    private void showCallListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View phoneNumberListRow = layoutInflater.inflate(R.layout.layout_call_phone_dialog, null);
        ListView callNumberListView = (ListView) phoneNumberListRow.findViewById(R.id.phone_number_list);

        callNumberListView.setAdapter(new PhoneNumberListAdapter(this, scannedInvoice.getInvoicePhoneNumbers()));
        builder.setView(phoneNumberListRow);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void printReceipt(){
        writeDetailsInfo = writeDetailsView.getText().toString();

        Intent intent = new Intent(FundActivity.this, PreviewReceiptActivity.class);
        intent.putExtra("scanned_invoice", scannedInvoice);
        intent.putExtra("funded_payment", fundedAmount);
        intent.putExtra("receipt_details_info", writeDetailsInfo);
        intent.putExtra("isCancel", isCancel);
        startActivity(intent);
    }

    @SuppressLint("DefaultLocale")
    private void showPayAmountDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View editAmountView = layoutInflater.inflate(R.layout.layout_fund_payment_dialog, null);
        builder.setView(editAmountView);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        EditText editAmountText = editAmountView.findViewById(R.id.edit_amount_text);
        Button paymentButton = editAmountView.findViewById(R.id.fund_payment_btn);

        paymentButton.setOnClickListener(v -> {
            fundedPaymentLayout.setVisibility(View.VISIBLE);
            writeDetailsLayout.setVisibility(View.VISIBLE);

            alertDialog.hide();

            callPhoneButton.setVisibility(View.GONE);
            fundPaymentButton.setVisibility(View.GONE);
            checkoutButton.setVisibility(View.VISIBLE);

            double fundedPaymentValue = Double.parseDouble(editAmountText.getText().toString());
            fundedAmount = editAmountText.getText().toString();
            fundedPaymentView.setText(String.format("$ %.2f", fundedPaymentValue));

            double paybackAmount = Math.round((Double.parseDouble(scannedInvoice.getInvoiceValue()) - fundedPaymentValue)*100)/100.0d;
            payBackAmountView.setText(String.format("$ %.2f", paybackAmount));

        });
    }

    private void showCancelDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancellable Receipt?").setMessage("");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            isCancel = "Y";
            printReceipt();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            isCancel = "N";
            printReceipt();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void setValues() {
        invoiceNameView.setText(scannedInvoice.getInvoiceName());
        invoiceNumberView.setText(scannedInvoice.getInvoiceNumber());
        invoiceValueView.setText(scannedInvoice.getInvoiceValue());
        invoiceIDView1.setText(scannedInvoice.getInvoiceId1());
        invoiceIDView2.setText(scannedInvoice.getInvoiceId2());
        invoiceAddressView.setText(scannedInvoice.getInvoiceAddress1() + " , " + scannedInvoice.getInvoiceAddress2());
        invoiceDateView.setText(scannedInvoice.getInvoiceDate());
    }

    private void initViews() {
        invoiceNameView = findViewById(R.id.invoice_name_view);
        invoiceNumberView = findViewById(R.id.invoice_number_view);
        invoiceAddressView = findViewById(R.id.invoice_address_view);
        invoiceIDView1 = findViewById(R.id.invoice_id1_view);
        invoiceIDView2 = findViewById(R.id.invoice_id2_view);
        invoiceValueView = findViewById(R.id.invoice_value_view);
        invoiceDateView = findViewById(R.id.invoice_date_view);

        fundedPaymentLayout = findViewById(R.id.fund_payment_layout);
        fundedPaymentView = findViewById(R.id.fund_payment_view);
        payBackAmountView = findViewById(R.id.payback_amount_view);

        writeDetailsLayout = findViewById(R.id.write_details_layout);
        writeDetailsView = findViewById(R.id.write_details_view);

        checkoutButton = findViewById(R.id.checkout_button);
        callPhoneButton = findViewById(R.id.call_phone_button);
        fundPaymentButton = findViewById(R.id.fund_payment_button);
    }
}