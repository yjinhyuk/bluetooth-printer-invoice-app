package com.example.bluetoothprinterinvoiceapp.database.entities;

import androidx.annotation.ColorLong;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Receipt {

    @PrimaryKey(autoGenerate = true)
    public int receiptid;

    public Receipt(){}

    public String getNUMAR_FACTURA() {
        return NUMAR_FACTURA;
    }

    public void setNUMAR_FACTURA(String NUMAR_FACTURA) {
        this.NUMAR_FACTURA = NUMAR_FACTURA;
    }

    public String getSERIE_CHITANTA() {
        return SERIE_CHITANTA;
    }

    public void setSERIE_CHITANTA(String SERIE_CHITANTA) {
        this.SERIE_CHITANTA = SERIE_CHITANTA;
    }

    public String getNUMAR_CHITANTA() {
        return NUMAR_CHITANTA;
    }

    public void setNUMAR_CHITANTA(String NUMAR_CHITANTA) {
        this.NUMAR_CHITANTA = NUMAR_CHITANTA;
    }

    public String getVALOARE_PLATA() {
        return VALOARE_PLATA;
    }

    public void setVALOARE_PLATA(String VALOARE_PLATA) {
        this.VALOARE_PLATA = VALOARE_PLATA;
    }

    public String getDATA_PLATA() {
        return DATA_PLATA;
    }

    public void setDATA_PLATA(String DATA_PLATA) {
        this.DATA_PLATA = DATA_PLATA;
    }

    public String getNUME_USER() {
        return NUME_USER;
    }

    public void setNUME_USER(String NUME_USER) {
        this.NUME_USER = NUME_USER;
    }

    public String getCHEIE_TRANSMISIE() {
        return CHEIE_TRANSMISIE;
    }

    public void setCHEIE_TRANSMISIE(String CHEIE_TRANSMISIE) {
        this.CHEIE_TRANSMISIE = CHEIE_TRANSMISIE;
    }

    public String getIS_CANCEL() {
        return IS_CANCEL;
    }

    public void setIS_CANCEL(String IS_CANCEL) {
        this.IS_CANCEL = IS_CANCEL;
    }

    public String getCOMENTARIU() {
        return COMENTARIU;
    }

    public void setCOMENTARIU(String COMENTARIU) {
        this.COMENTARIU = COMENTARIU;
    }

    @ColumnInfo(name = "NUMAR_FACTURA")
    public String NUMAR_FACTURA;

    @ColumnInfo(name = "SERIE_CHITANTA")
    public String SERIE_CHITANTA;

    @ColumnInfo(name = "NUMAR_CHITANTA")
    public String NUMAR_CHITANTA;

    @ColumnInfo(name = "VALOARE_PLATA")
    public String VALOARE_PLATA;

    @ColumnInfo(name = "DATA_PLATA")
    public String DATA_PLATA;

    @ColumnInfo(name = "NUME_USER")
    public String NUME_USER;

    @ColumnInfo(name = "CHEIE_TRANSMISIE")
    public String CHEIE_TRANSMISIE;

    @ColumnInfo(name = "IS_CANCEL")
    public String IS_CANCEL;

    @ColumnInfo(name = "COMENTARIU")
    public String COMENTARIU;

}
