package com.orderready.backend.controller;
import com.orderready.backend.dto.UpdateQuantityRequest;
import com.orderready.backend.entity.Inventory;
import com.orderready.backend.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryRepository inventoryRepository;

    @GetMapping
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }
    // PUT /api/inventory/{id} - bir malzemenin stok miktarını günceller
    @PutMapping("/{id}")
    public Inventory updateInventory(@PathVariable Long id, @RequestBody UpdateQuantityRequest request) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stok kaydı bulunamadı"));
        inventory.setCurrentQuantity(request.getQuantity());
        return inventoryRepository.save(inventory);
    }
}
