package com.orderready.backend.dto;

import java.math.BigDecimal;

// depo çalışanının elle stok ekleme/çıkarma isteğini taşır
public class StockAdjustmentRequest {
    private BigDecimal quantity;
    private String type; // ADDITION veya REMOVAL

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
