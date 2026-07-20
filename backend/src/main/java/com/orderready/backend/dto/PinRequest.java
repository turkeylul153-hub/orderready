package com.orderready.backend.dto;

// yetkili işlemler için PIN kodu taşır
public class PinRequest {
    private String pin;

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
}