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

// bir siparişin üretilebilirliğini (üretim süresi, eksik malzeme, tedarikçi, teslim aralığı) hesaplar
@Service
public class FeasibilityService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private MaterialStockTransactionRepository materialTxRepository;

    @Autowired
    private SupplierMaterialRepository supplierMaterialRepository;

    public FeasibilityResponse checkFeasibility(FeasibilityRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));

        List<Recipe> recipes = recipeRepository.findByProduct_Id(product.getId());
        List<MaterialShortfall> shortfalls = new ArrayList<>();
        boolean hasShortfall = false;

        for (Recipe recipe : recipes) {
            // istenen miktar için gereken malzeme miktarını hesapla
            BigDecimal required = recipe.getQuantityPer100kg()
                    .multiply(request.getQuantityKg())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // fabrikadaki güncel stoğu bul
            BigDecimal factoryStock = materialTxRepository
                    .findFirstByMaterial_IdOrderByCreatedAtDesc(recipe.getMaterial().getId())
                    .map(MaterialStockTransaction::getBalanceAfter)
                    .orElse(BigDecimal.ZERO);

            if (factoryStock.compareTo(required) < 0) {
                // eksik var
                hasShortfall = true;
                BigDecimal shortfallAmount = required.subtract(factoryStock);

                MaterialShortfall shortfall = new MaterialShortfall();
                shortfall.setMaterialName(recipe.getMaterial().getName());
                shortfall.setUnit(recipe.getMaterial().getUnit());
                shortfall.setRequiredQuantity(required);
                shortfall.setFactoryStock(factoryStock);
                shortfall.setShortfall(shortfallAmount);

                // bu malzemeyi sağlayan tedarikçiyi bul (varsa ilkini kullan)
                List<SupplierMaterial> suppliers = supplierMaterialRepository.findByMaterial_Id(recipe.getMaterial().getId());
                if (!suppliers.isEmpty()) {
                    shortfall.setSupplierName(suppliers.get(0).getSupplier().getName());
                } else {
                    shortfall.setSupplierName("Tedarikçi tanımlı değil");
                }

                shortfalls.add(shortfall);
            }
        }

        // üretim süresini hesapla (saat cinsinden)
        BigDecimal productionTimeHours = request.getQuantityKg()
                .divide(product.getProductionRateKgPerHour(), 2, RoundingMode.HALF_UP);

        // teslim aralığını belirle
        String deliveryEstimate;
        if (!hasShortfall) {
            // eksik yok, sadece üretim süresine göre aralık
            if (productionTimeHours.compareTo(BigDecimal.valueOf(24)) <= 0) {
                deliveryEstimate = "Bugün - 1 iş günü içinde";
            } else {
                deliveryEstimate = "1-2 iş günü içinde";
            }
        } else {
            // eksik var, tedarik beklenmesi gerektiği için daha geniş aralık
            deliveryEstimate = "2-4 iş günü içinde";
        }

        FeasibilityResponse response = new FeasibilityResponse();
        response.setProductName(product.getName());
        response.setRequestedQuantity(request.getQuantityKg());
        response.setProductionTimeHours(productionTimeHours);
        response.setHasShortfall(hasShortfall);
        response.setDeliveryEstimateText(deliveryEstimate);
        response.setMaterialShortfalls(shortfalls);

        return response;
    }
}
