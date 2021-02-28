package com.example.bluetoothprinterinvoiceapp.models;

import java.io.Serializable;
import java.util.List;

public class Invoice implements Serializable {
    public String invoiceName;
    public String invoiceAddress1;
    public String invoiceAddress2;
    public String invoiceId1;
    public String invoiceId2;
    public List<String> invoicePhoneNumbers;
    public String invoiceNumber;
    public String invoiceValue;
    public String invoiceDate;

    public Invoice(String _name, String _address1, String _address2, String _id1, String _id2, List<String> _phoneNumbers, String _invoiceNumber, String _invoiceValue, String _date){
        invoiceName = _name;
        invoiceAddress1 = _address1;
        invoiceAddress2 = _address2;
        invoiceId1 = _id1;
        invoiceId2 = _id2;
        invoicePhoneNumbers = _phoneNumbers;
        invoiceNumber = _invoiceNumber;
        invoiceValue = _invoiceValue;
        invoiceDate = _date;
    }


    public String getInvoiceName() {
        return invoiceName;
    }

    public void setInvoiceName(String name) {
        this.invoiceName = name;
    }



    public String getInvoiceAddress1() {
        return invoiceAddress1;
    }

    public void setInvoiceAddress1(String address1) {
        this.invoiceAddress1 = address1;
    }



    public String getInvoiceAddress2() {
        return invoiceAddress2;
    }

    public void setInvoiceAddress2(String address2) {
        this.invoiceAddress2 = address2;
    }



    public String getInvoiceId1() {
        return invoiceId1;
    }

    public void setInvoiceId1(String id1) {
        this.invoiceId1 = id1;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setDate(String date) {
        this.invoiceDate = date;
    }

    public String getInvoiceValue() {
        return invoiceValue;
    }

    public void setInvoiceValue(String invoiceValue) {
        this.invoiceValue = invoiceValue;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public List<String> getInvoicePhoneNumbers() {
        return invoicePhoneNumbers;
    }

    public void setInvoicePhoneNumbers(List<String> phoneNumbers) {
        this.invoicePhoneNumbers = phoneNumbers;
    }

    public String getInvoiceId2() {
        return invoiceId2;
    }

    public void setInvoiceId2(String id2) {
        this.invoiceId2 = id2;
    }



}
