package com.orderready.backend.service;

import com.orderready.backend.dto.MaterialStatus;
import com.orderready.backend.dto.OrderCalculationRequest;
import com.orderready.backend.dto.OrderCalculationResponse;
import com.orderready.backend.entity.Inventory;
import com.orderready.backend.entity.Product;
import com.orderready.backend.entity.Recipe;
import com.orderready.backend.entity.SupplierMaterial;
import com.orderready.backend.repository.InventoryRepository;
import com.orderready.backend.repository.ProductRepository;
import com.orderready.backend.repository.RecipeRepository;
import com.orderready.backend.repository.SupplierMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal; // ondalıklı sayı hassasiyetini korumak için
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

// bir sipariş için gereken malzemeleri, tedarik süresini ve üretim süresini hesaplayan servis
@Service
public class OrderCalculationService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private SupplierMaterialRepository supplierMaterialRepository;

    // sabit kargo süresi: tedarikçide hazır stok varsa, gelmesi bu kadar gün sürer
    private static final BigDecimal SHIPPING_DAYS_IF_SUPPLIER_HAS_STOCK = BigDecimal.ONE;

    public OrderCalculationResponse calculate(OrderCalculationRequest request) {
        // 1) ürünü bul
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));

        // 2) bu ürünün reçetesindeki tüm malzeme satırlarını bul
        List<Recipe> recipes = recipeRepository.findByProduct_Id(product.getId());

        List<MaterialStatus> materialStatuses = new ArrayList<>();
        BigDecimal maxSupplyDays = BigDecimal.ZERO;

        // 3) her reçete satırı (her malzeme) için hesaplama yap
        for (Recipe recipe : recipes) {
            MaterialStatus status = new MaterialStatus();
            status.setMaterialName(recipe.getMaterial().getName());
            status.setUnit(recipe.getMaterial().getUnit());

            // istenen miktar için gereken malzeme miktarını hesapla
            BigDecimal required = recipe.getQuantityPer100kg()
                    .multiply(request.getQuantityKg())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP); // bölme işleminde, 2 ondalık basamağa yuvarla yarımsa yukarı yubarla
            status.setRequiredQuantity(required);

            // fabrika deposundaki mevcut stoğu bul
            BigDecimal factoryStock = inventoryRepository.findByMaterial_Id(recipe.getMaterial().getId())
                    .map(Inventory::getCurrentQuantity)
                    .orElse(BigDecimal.ZERO);// stok kaydı yoksa sıfır kabul et ( hiç insert yapılmamış bir malzeme için hatta almamak için
            status.setFactoryStock(factoryStock);

            if (factoryStock.compareTo(required) >= 0) {
                // fabrikada yeterli var, hiçbir eksik yok
                status.setShortfall(BigDecimal.ZERO);
                status.setSupplierHasStock(false);
                status.setEstimatedSupplyDays(BigDecimal.ZERO);
            } else {
                // fabrikada eksik var, tedarikçiye bakmamız lazım
                BigDecimal shortfall = required.subtract(factoryStock);
                status.setShortfall(shortfall);

                List<SupplierMaterial> supplierOptions = supplierMaterialRepository.findByMaterial_Id(recipe.getMaterial().getId());

                if (supplierOptions.isEmpty()) {
                    // bu malzeme için hiç tedarikçi kaydı yok
                    status.setSupplierName("Tedarikçi bulunamadı");
                    status.setSupplierHasStock(false);
                    status.setEstimatedSupplyDays(null);
                } else {
                    // basitlik için ilk tedarikçiyi kullanıyoruz
                    SupplierMaterial supplierMaterial = supplierOptions.get(0);
                    status.setSupplierName(supplierMaterial.getSupplier().getName());

                    if (supplierMaterial.getAvailableQuantity().compareTo(shortfall) >= 0) {
                        // tedarikçide hazır stok yeterli, sadece kargo süresi beklenir
                        status.setSupplierHasStock(true);
                        status.setEstimatedSupplyDays(SHIPPING_DAYS_IF_SUPPLIER_HAS_STOCK);
                    } else {
                        // tedarikçide de yeterli yok, tahmini üretim+gönderim süresi hesaplanır
                        status.setSupplierHasStock(false);
                        BigDecimal estimatedDays = shortfall
                                .divide(supplierMaterial.getLeadTimeBatchSize(), 4, RoundingMode.HALF_UP)
                                .multiply(supplierMaterial.getLeadTimeDays());
                        status.setEstimatedSupplyDays(estimatedDays);
                    }
                }

                // en uzun tedarik süresini takip et
                if (status.getEstimatedSupplyDays() != null && status.getEstimatedSupplyDays().compareTo(maxSupplyDays) > 0) {
                    maxSupplyDays = status.getEstimatedSupplyDays();
                }
            }

            materialStatuses.add(status);
        }

        // 4) üretim süresini hesapla (saat cinsinden)
        BigDecimal productionTimeHours = request.getQuantityKg()
                .divide(product.getProductionRateKgPerHour(), 2, RoundingMode.HALF_UP);

        // 5) toplam süreyi hesapla (tedarik süresi gün -> saate çevrilip üretim süresine eklenir)
        BigDecimal supplyTimeHours = maxSupplyDays.multiply(BigDecimal.valueOf(24));
        BigDecimal totalTimeHours = productionTimeHours.add(supplyTimeHours);

        // 6) cevabı oluştur
        OrderCalculationResponse response = new OrderCalculationResponse();
        response.setProductName(product.getName());
        response.setRequestedQuantity(request.getQuantityKg());
        response.setProductionTimeHours(productionTimeHours);
        response.setSupplyTimeDays(maxSupplyDays);
        response.setTotalTimeHours(totalTimeHours);
        response.setMaterialStatuses(materialStatuses);

        return response;
    }
}