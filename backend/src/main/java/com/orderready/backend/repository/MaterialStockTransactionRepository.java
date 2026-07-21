package com.orderready.backend.repository;

import com.orderready.backend.entity.MaterialStockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaterialStockTransactionRepository extends JpaRepository<MaterialStockTransaction, Long> {
    List<MaterialStockTransaction> findByMaterial_IdOrderByCreatedAtDesc(Long materialId);
    Optional<MaterialStockTransaction> findFirstByMaterial_IdOrderByCreatedAtDesc(Long materialId);
    List<MaterialStockTransaction> findAllByOrderByCreatedAtDesc();
    boolean existsByOrder_Id(Long orderId);
}