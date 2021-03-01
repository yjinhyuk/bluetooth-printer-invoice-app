package com.example.bluetoothprinterinvoiceapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.bluetoothprinterinvoiceapp.database.entities.Receipt;

import java.util.List;

@Dao
public interface ReceiptDao {
    @Query("SELECT * FROM receipt")
    List<Receipt> getAll();

    @Query("SELECT * FROM receipt WHERE receiptid IN (:receiptIds)")
    List<Receipt> loadAllByIds(int[] receiptIds);

    @Query("SELECT * FROM receipt WHERE NUMAR_FACTURA LIKE :NUMAR_FACTURA ")
    Receipt findByInvoiceNumber(String NUMAR_FACTURA);

    @Insert
    void insert(Receipt receipt);

    @Delete
    void delete(Receipt receipt);
}
