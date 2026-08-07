package com.orderready.backend.controller;

import com.orderready.backend.dto.StockAdjustmentRequest;
import com.orderready.backend.entity.*;
import com.orderready.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/product-stock")
public class ProductStockController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductStockTransactionRepository transactionRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @GetMapping
    public List<ProductStockTransaction> getCurrentStock() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> transactionRepository
                        .findFirstByProduct_IdOrderByCreatedAtDesc(product.getId())
                        .orElseGet(() -> {
                            ProductStockTransaction emptyTx = new ProductStockTransaction();
                            emptyTx.setProduct(product);
                            emptyTx.setBalanceAfter(BigDecimal.ZERO);
                            emptyTx.setQuantityChange(BigDecimal.ZERO);
                            emptyTx.setType("NONE");
                            return emptyTx;
                        }))
                .toList();
    }

    @GetMapping("/history")
    public Page<ProductStockTransaction> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return transactionRepository.findAll(pageable);
    }

    @PostMapping("/{productId}/adjust")
    public ProductStockTransaction adjustStock(@PathVariable Long productId, @RequestBody StockAdjustmentRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));

        Warehouse warehouse = warehouseRepository.findAll().get(0);

        BigDecimal currentBalance = transactionRepository
                .findFirstByProduct_IdOrderByCreatedAtDesc(productId)
                .map(ProductStockTransaction::getBalanceAfter)
                .orElse(BigDecimal.ZERO);

        BigDecimal change = "REMOVAL".equals(request.getType())
                ? request.getQuantity().negate()
                : request.getQuantity();

        BigDecimal newBalance = currentBalance.add(change);

        ProductStockTransaction tx = new ProductStockTransaction();
        tx.setProduct(product);
        tx.setWarehouse(warehouse);
        tx.setQuantityChange(change);
        tx.setBalanceAfter(newBalance);
        tx.setType(request.getType());
        tx.setCreatedAt(LocalDateTime.now());

        return transactionRepository.save(tx);
    }
}
