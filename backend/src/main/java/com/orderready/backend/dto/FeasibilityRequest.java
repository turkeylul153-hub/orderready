package com.orderready.backend.dto;

import java.math.BigDecimal;

// ürün ve miktar bilgisiyle uygunluk kontrolü isteği
public class FeasibilityRequest {
    private Long productId;
    private BigDecimal quantityKg;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public BigDecimal getQuantityKg() { return quantityKg; }
    public void setQuantityKg(BigDecimal quantityKg) { this.quantityKg = quantityKg; }
}
