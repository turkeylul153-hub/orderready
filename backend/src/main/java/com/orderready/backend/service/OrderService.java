package com.orderready.backend.service;

import com.orderready.backend.entity.*;
import com.orderready.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

// sipariş durumu değişikliklerini ve buna bağlı stok hareketlerini yönetir
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private MaterialStockTransactionRepository materialTxRepository;

    @Autowired
    private ProductStockTransactionRepository productTxRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Value("${authorization.pin}")
    private String authorizationPin;
    private void verifyPin(String pin) {
        if (!authorizationPin.equals(pin)) {
            throw new RuntimeException("Yetkisiz işlem: PIN kodu hatalı");
        }
    }
    // siparişi üretime başlatır: önce stok yeterliliğini kontrol eder, sonra hammaddeyi düşer
    public Order startProduction(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));

        Warehouse warehouse = warehouseRepository.findAll().get(0);

        List<Recipe> recipes = recipeRepository.findByProduct_Id(order.getProduct().getId());

        // 1. Adım: Önce tüm malzemelerin yeterli olup olmadığını kontrol et
        for (Recipe recipe : recipes) {
            BigDecimal required = recipe.getQuantityPer100kg()
                    .multiply(order.getQuantityKg())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal currentBalance = materialTxRepository
                    .findFirstByMaterial_IdOrderByCreatedAtDesc(recipe.getMaterial().getId())
                    .map(MaterialStockTransaction::getBalanceAfter)
                    .orElse(BigDecimal.ZERO);

            if (currentBalance.compareTo(required) < 0) {
                throw new RuntimeException(
                        "Yetersiz stok: " + recipe.getMaterial().getName() +
                                " (gereken: " + required + ", mevcut: " + currentBalance + ")"
                );
            }
        }

        // 2. Adım: Kontrol geçildi, şimdi gerçekten düşüş yap
        for (Recipe recipe : recipes) {
            BigDecimal required = recipe.getQuantityPer100kg()
                    .multiply(order.getQuantityKg())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal currentBalance = materialTxRepository
                    .findFirstByMaterial_IdOrderByCreatedAtDesc(recipe.getMaterial().getId())
                    .map(MaterialStockTransaction::getBalanceAfter)
                    .orElse(BigDecimal.ZERO);

            BigDecimal newBalance = currentBalance.subtract(required);

            MaterialStockTransaction tx = new MaterialStockTransaction();
            tx.setMaterial(recipe.getMaterial());
            tx.setWarehouse(warehouse);
            tx.setQuantityChange(required.negate());
            tx.setBalanceAfter(newBalance);
            tx.setType("ORDER_CONSUMPTION");
            tx.setOrder(order);
            tx.setCreatedAt(LocalDateTime.now());
            materialTxRepository.save(tx);
        }

        order.setStatus("IN_PRODUCTION");
        return orderRepository.save(order);
    }

    // üretime başlatmayı geri alır: hammaddeyi geri ekler, durumu PENDING'e döndürür
    public Order revertProduction(Long orderId, String pin) {
        verifyPin(pin);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));

        Warehouse warehouse = warehouseRepository.findAll().get(0);
        List<Recipe> recipes = recipeRepository.findByProduct_Id(order.getProduct().getId());

        for (Recipe recipe : recipes) {
            BigDecimal required = recipe.getQuantityPer100kg()
                    .multiply(order.getQuantityKg())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal currentBalance = materialTxRepository
                    .findFirstByMaterial_IdOrderByCreatedAtDesc(recipe.getMaterial().getId())
                    .map(MaterialStockTransaction::getBalanceAfter)
                    .orElse(BigDecimal.ZERO);

            BigDecimal newBalance = currentBalance.add(required);

            MaterialStockTransaction tx = new MaterialStockTransaction();
            tx.setMaterial(recipe.getMaterial());
            tx.setWarehouse(warehouse);
            tx.setQuantityChange(required);
            tx.setBalanceAfter(newBalance);
            tx.setType("ORDER_REVERSAL");
            tx.setOrder(order);
            tx.setCreatedAt(LocalDateTime.now());
            materialTxRepository.save(tx);
        }

        order.setStatus("PENDING");
        return orderRepository.save(order);
    }

    public Order revertShipment(Long orderId , String pin) {
        verifyPin(pin);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));

        Warehouse warehouse = warehouseRepository.findAll().get(0);

        BigDecimal currentBalance = productTxRepository
                .findFirstByProduct_IdOrderByCreatedAtDesc(order.getProduct().getId())
                .map(ProductStockTransaction::getBalanceAfter)
                .orElse(BigDecimal.ZERO);

        BigDecimal newBalance = currentBalance.add(order.getQuantityKg());
        ProductStockTransaction tx = new ProductStockTransaction();
        tx.setProduct(order.getProduct());
        tx.setWarehouse(warehouse);
        tx.setQuantityChange(order.getQuantityKg());
        tx.setBalanceAfter(newBalance);
        tx.setType("ORDER_REVERSAL");
        tx.setOrder(order);
        tx.setCreatedAt(LocalDateTime.now());
        productTxRepository.save(tx);
        order.setStatus("COMPLETED");
        return orderRepository.save(order);
    }

        // üretimi tamamlar: durumu değiştirir ve bitmiş ürünü stoğa ekler
    public Order completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));

        Warehouse warehouse = warehouseRepository.findAll().get(0);

        BigDecimal currentBalance = productTxRepository
                .findFirstByProduct_IdOrderByCreatedAtDesc(order.getProduct().getId())
                .map(ProductStockTransaction::getBalanceAfter)
                .orElse(BigDecimal.ZERO);

        BigDecimal newBalance = currentBalance.add(order.getQuantityKg());

        ProductStockTransaction tx = new ProductStockTransaction();
        tx.setProduct(order.getProduct());
        tx.setWarehouse(warehouse);
        tx.setQuantityChange(order.getQuantityKg());
        tx.setBalanceAfter(newBalance);
        tx.setType("ORDER_COMPLETION");
        tx.setOrder(order);
        tx.setCreatedAt(LocalDateTime.now());
        productTxRepository.save(tx);

        order.setStatus("COMPLETED");
        return orderRepository.save(order);
    }

    // gönderimi kaydeder: durumu değiştirir ve ürünü stoktan düşer
    public Order shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));

        Warehouse warehouse = warehouseRepository.findAll().get(0);

        BigDecimal currentBalance = productTxRepository
                .findFirstByProduct_IdOrderByCreatedAtDesc(order.getProduct().getId())
                .map(ProductStockTransaction::getBalanceAfter)
                .orElse(BigDecimal.ZERO);

        BigDecimal newBalance = currentBalance.subtract(order.getQuantityKg());
        ProductStockTransaction tx = new ProductStockTransaction();
        tx.setProduct(order.getProduct());
        tx.setWarehouse(warehouse);
        tx.setQuantityChange(order.getQuantityKg().negate());
        tx.setBalanceAfter(newBalance);
        tx.setType("ORDER_SHIPMENT");
        tx.setOrder(order);
        tx.setCreatedAt(LocalDateTime.now());
        productTxRepository.save(tx);
        order.setStatus("SHIPPED");
        return orderRepository.save(order);
    }
}
