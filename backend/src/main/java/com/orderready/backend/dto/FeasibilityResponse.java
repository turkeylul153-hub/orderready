package com.orderready.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class FeasibilityResponse {
    private String productName;
    private BigDecimal requestedQuantity;
    private BigDecimal productionTimeHours;
    private boolean hasShortfall;
    private boolean fullyCoveredByStock;
    private String deliveryEstimateText;
    private List<MaterialShortfall> materialShortfalls;

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getRequestedQuantity() { return requestedQuantity; }
    public void setRequestedQuantity(BigDecimal requestedQuantity) { this.requestedQuantity = requestedQuantity; }

    public BigDecimal getProductionTimeHours() { return productionTimeHours; }
    public void setProductionTimeHours(BigDecimal productionTimeHours) { this.productionTimeHours = productionTimeHours; }

    public boolean isHasShortfall() { return hasShortfall; }
    public void setHasShortfall(boolean hasShortfall) { this.hasShortfall = hasShortfall; }

    public boolean isFullyCoveredByStock() { return fullyCoveredByStock; }
    public void setFullyCoveredByStock(boolean fullyCoveredByStock) { this.fullyCoveredByStock = fullyCoveredByStock; }

    public String getDeliveryEstimateText() { return deliveryEstimateText; }
    public void setDeliveryEstimateText(String deliveryEstimateText) { this.deliveryEstimateText = deliveryEstimateText; }

    public List<MaterialShortfall> getMaterialShortfalls() { return materialShortfalls; }
    public void setMaterialShortfalls(List<MaterialShortfall> materialShortfalls) { this.materialShortfalls = materialShortfalls; }
}