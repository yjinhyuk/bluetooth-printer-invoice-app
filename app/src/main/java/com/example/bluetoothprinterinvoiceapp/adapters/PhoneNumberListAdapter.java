package com.example.bluetoothprinterinvoiceapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bluetoothprinterinvoiceapp.R;

import java.util.List;

public class PhoneNumberListAdapter extends BaseAdapter {
    List<String> phoneNumbers;
    Context context;

    public PhoneNumberListAdapter(Context _context, List<String> _phoneNumbers){
        this.context = _context;
        this.phoneNumbers = _phoneNumbers;
    }
    @Override
    public int getCount() {
        return phoneNumbers.size();
    }

    @Override
    public Object getItem(int position) {
        return phoneNumbers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View view = inflater.inflate(R.layout.layout_phone_num_list_item, parent, false);
        TextView phoneNumberView = view.findViewById(R.id.call_phone_number_view);
        String callNum = this.phoneNumbers.get(position);
        phoneNumberView.setText(this.phoneNumbers.get(position));
        phoneNumberView.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+callNum));
            context.startActivity(callIntent);
        });
        return view;
    }
}
