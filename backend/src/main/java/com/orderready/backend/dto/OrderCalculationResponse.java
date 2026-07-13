package com.orderready.backend.dto;

import java.math.BigDecimal;
import java.util.List;

// hesaplama motorunun ürettiği tam sonucu taşır
public class OrderCalculationResponse {
    private String productName;
    private BigDecimal requestedQuantity;
    private BigDecimal productionTimeHours;
    private BigDecimal supplyTimeDays;
    private BigDecimal totalTimeHours;
    private List<MaterialStatus> materialStatuses;

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getRequestedQuantity() { return requestedQuantity; }
    public void setRequestedQuantity(BigDecimal requestedQuantity) { this.requestedQuantity = requestedQuantity; }

    public BigDecimal getProductionTimeHours() { return productionTimeHours; }
    public void setProductionTimeHours(BigDecimal productionTimeHours) { this.productionTimeHours = productionTimeHours; }

    public BigDecimal getSupplyTimeDays() { return supplyTimeDays; }
    public void setSupplyTimeDays(BigDecimal supplyTimeDays) { this.supplyTimeDays = supplyTimeDays; }

    public BigDecimal getTotalTimeHours() { return totalTimeHours; }
    public void setTotalTimeHours(BigDecimal totalTimeHours) { this.totalTimeHours = totalTimeHours; }

    public List<MaterialStatus> getMaterialStatuses() { return materialStatuses; }
    public void setMaterialStatuses(List<MaterialStatus> materialStatuses) { this.materialStatuses = materialStatuses; }
}