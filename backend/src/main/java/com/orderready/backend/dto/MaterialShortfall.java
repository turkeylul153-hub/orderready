package com.orderready.backend.dto;

import java.math.BigDecimal;

// bir malzemenin eksik durumunu ve hangi tedarikçiden karşılanabileceğini taşır
public class MaterialShortfall {
    private String materialName;
    private String unit;
    private BigDecimal requiredQuantity;
    private BigDecimal factoryStock;
    private BigDecimal shortfall;
    private String supplierName;

    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(BigDecimal requiredQuantity) { this.requiredQuantity = requiredQuantity; }

    public BigDecimal getFactoryStock() { return factoryStock; }
    public void setFactoryStock(BigDecimal factoryStock) { this.factoryStock = factoryStock; }

    public BigDecimal getShortfall() { return shortfall; }
    public void setShortfall(BigDecimal shortfall) { this.shortfall = shortfall; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
}
