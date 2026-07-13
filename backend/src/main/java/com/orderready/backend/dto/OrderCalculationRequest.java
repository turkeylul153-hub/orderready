package com.orderready.backend.dto;

import java.math.BigDecimal;

// üretim planlamacının gönderdiği istek: hangi ürün, ne kadar miktar
public class OrderCalculationRequest {
    private Long productId;
    private BigDecimal quantityKg;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public BigDecimal getQuantityKg() { return quantityKg; }
    public void setQuantityKg(BigDecimal quantityKg) { this.quantityKg = quantityKg; }
}