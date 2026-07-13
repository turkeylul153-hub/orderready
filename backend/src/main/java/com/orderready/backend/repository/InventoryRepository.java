package com.orderready.backend.repository;

import com.orderready.backend.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// inventory için temel veritabanı işlemlerini sağlar
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // belirli bir malzemenin stok kaydını getirir
    Optional<Inventory> findByMaterial_Id(Long materialId);
}