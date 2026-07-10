package com.orderready.backend.repository;

import com.orderready.backend.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

// Inventory için temel veritabanı işlemleri
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}