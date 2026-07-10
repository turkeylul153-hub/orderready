package com.orderready.backend.dto;

import java.math.BigDecimal;

// depo çalışanının gönderdiği yeni stok miktarını taşıyan basit veri taşıyıcı
public class UpdateQuantityRequest {
    private BigDecimal quantity;

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
}
