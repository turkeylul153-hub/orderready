package com.orderready.backend.dto;

// sipariş iptali için PIN ve zorunlu açıklama taşır
public class CancelRequest {
    private String pin;
    private String reason;

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}