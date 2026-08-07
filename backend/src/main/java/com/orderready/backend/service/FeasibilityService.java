package com.orderready.backend.service;

import com.orderready.backend.dto.FeasibilityRequest;
import com.orderready.backend.dto.FeasibilityResponse;
import com.orderready.backend.dto.MaterialShortfall;
import com.orderready.backend.entity.*;
import com.orderready.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeasibilityService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private MaterialStockTransactionRepository materialTxRepository;

    @Autowired
    private ProductStockTransactionRepository productTxRepository;

    @Autowired
    private SupplierMaterialRepository supplierMaterialRepository;

    public FeasibilityResponse checkFeasibility(FeasibilityRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));

        // Önce depoda hazır ürün var mı kontrol et
        BigDecimal totalProductStock = productTxRepository
                .findFirstByProduct_IdOrderByCreatedAtDesc(product.getId())
                .map(ProductStockTransaction::getBalanceAfter)
                .orElse(BigDecimal.ZERO);

        List<Order> reservedOrders = orderRepository.findByProduct_IdAndStatus(product.getId(), "COMPLETED");
        BigDecimal reservedQuantity = reservedOrders.stream()
                .map(Order::getQuantityKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal availableProductStock = totalProductStock.subtract(reservedQuantity);

        boolean fullyCoveredByStock = availableProductStock.compareTo(request.getQuantityKg()) >= 0;
        FeasibilityResponse response = new FeasibilityResponse();
        response.setProductName(product.getName());
        response.setRequestedQuantity(request.getQuantityKg());
        response.setFullyCoveredByStock(fullyCoveredByStock);

        if (fullyCoveredByStock) {
            response.setProductionTimeHours(BigDecimal.ZERO);
            response.setHasShortfall(false);
            response.setDeliveryEstimateText("Depoda mevcut, hemen gönderilebilir");
            response.setMaterialShortfalls(new ArrayList<>());
            return response;
        }

        List<Recipe> recipes = recipeRepository.findByProduct_Id(product.getId());
        List<MaterialShortfall> shortfalls = new ArrayList<>();
        boolean hasShortfall = false;

        for (Recipe recipe : recipes) {
            BigDecimal required = recipe.getQuantityPer100kg()
                    .multiply(request.getQuantityKg())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal factoryStock = materialTxRepository
                    .findFirstByMaterial_IdOrderByCreatedAtDesc(recipe.getMaterial().getId())
                    .map(MaterialStockTransaction::getBalanceAfter)
                    .orElse(BigDecimal.ZERO);

            if (factoryStock.compareTo(required) < 0) {
                hasShortfall = true;
                BigDecimal shortfallAmount = required.subtract(factoryStock);

                MaterialShortfall shortfall = new MaterialShortfall();
                shortfall.setMaterialName(recipe.getMaterial().getName());
                shortfall.setUnit(recipe.getMaterial().getUnit());
                shortfall.setRequiredQuantity(required);
                shortfall.setFactoryStock(factoryStock);
                shortfall.setShortfall(shortfallAmount);

                List<SupplierMaterial> suppliers = supplierMaterialRepository.findByMaterial_Id(recipe.getMaterial().getId());
                if (!suppliers.isEmpty()) {
                    shortfall.setSupplierName(suppliers.get(0).getSupplier().getName());
                } else {
                    shortfall.setSupplierName("Tedarikçi tanımlı değil");
                }

                shortfalls.add(shortfall);
            }
        }

        BigDecimal productionTimeHours = request.getQuantityKg()
                .divide(product.getProductionRateKgPerHour(), 2, RoundingMode.HALF_UP);

        String deliveryEstimate;
        if (!hasShortfall) {
            if (productionTimeHours.compareTo(BigDecimal.valueOf(24)) <= 0) {
                deliveryEstimate = "Bugün - 1 iş günü içinde";
            } else {
                deliveryEstimate = "1-2 iş günü içinde";
            }
        } else {
            BigDecimal maxShortfallRatio = BigDecimal.ZERO;
            for (MaterialShortfall shortfall : shortfalls) {
                BigDecimal stockBase = shortfall.getFactoryStock().compareTo(BigDecimal.ZERO) > 0
                        ? shortfall.getFactoryStock()
                        : BigDecimal.ONE;
                BigDecimal ratio = shortfall.getShortfall().divide(stockBase, 4, RoundingMode.HALF_UP);
                if (ratio.compareTo(maxShortfallRatio) > 0) {
                    maxShortfallRatio = ratio;
                }
            }

            if (maxShortfallRatio.compareTo(BigDecimal.valueOf(0.2)) <= 0) {
                deliveryEstimate = "2-3 iş günü içinde";
            } else if (maxShortfallRatio.compareTo(BigDecimal.valueOf(1)) <= 0) {
                deliveryEstimate = "3-5 iş günü içinde";
            } else {
                deliveryEstimate = "1-2 hafta içinde, tedarikçiyle görüşülmeli";
            }
        }

        response.setProductionTimeHours(productionTimeHours);
        response.setHasShortfall(hasShortfall);
        response.setDeliveryEstimateText(deliveryEstimate);
        response.setMaterialShortfalls(shortfalls);

        return response;
    }
}