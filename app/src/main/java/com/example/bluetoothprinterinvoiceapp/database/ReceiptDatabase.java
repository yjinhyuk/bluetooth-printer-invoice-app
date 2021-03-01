package com.example.bluetoothprinterinvoiceapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.bluetoothprinterinvoiceapp.database.dao.ReceiptDao;
import com.example.bluetoothprinterinvoiceapp.database.entities.Receipt;

@Database(entities = {Receipt.class}, version = 1)
public abstract class ReceiptDatabase extends RoomDatabase {
    public abstract ReceiptDao receiptDao();
}
