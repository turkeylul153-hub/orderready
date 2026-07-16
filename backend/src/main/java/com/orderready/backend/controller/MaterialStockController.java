package com.orderready.backend.controller;

import com.orderready.backend.dto.StockAdjustmentRequest;
import com.orderready.backend.entity.*;
import com.orderready.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/material-stock")
public class MaterialStockController {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MaterialStockTransactionRepository transactionRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    // GET /api/material-stock - her malzemenin güncel stoğunu listeler
    @GetMapping
    public List<MaterialStockTransaction> getCurrentStock() {
        List<Material> materials = materialRepository.findAll();
        return materials.stream()
                .map(material -> transactionRepository
                        .findFirstByMaterial_IdOrderByCreatedAtDesc(material.getId())
                        .orElse(null))
                .filter(tx -> tx != null)
                .toList();
    }

    // POST /api/material-stock/{materialId}/adjust - elle stok ekleme/çıkarma
    @PostMapping("/{materialId}/adjust")
    public MaterialStockTransaction adjustStock(@PathVariable Long materialId, @RequestBody StockAdjustmentRequest request) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Malzeme bulunamadı"));

        Warehouse warehouse = warehouseRepository.findAll().get(0);

        BigDecimal currentBalance = transactionRepository
                .findFirstByMaterial_IdOrderByCreatedAtDesc(materialId)
                .map(MaterialStockTransaction::getBalanceAfter)
                .orElse(BigDecimal.ZERO);

        // ADDITION ise pozitif, REMOVAL ise negatif değişim uygula
        BigDecimal change = "REMOVAL".equals(request.getType())
                ? request.getQuantity().negate()
                : request.getQuantity();

        BigDecimal newBalance = currentBalance.add(change);

        MaterialStockTransaction tx = new MaterialStockTransaction();
        tx.setMaterial(material);
        tx.setWarehouse(warehouse);
        tx.setQuantityChange(change);
        tx.setBalanceAfter(newBalance);
        tx.setType(request.getType());
        tx.setCreatedAt(LocalDateTime.now());

        return transactionRepository.save(tx);
    }
}
