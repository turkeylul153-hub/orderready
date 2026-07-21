package com.orderready.backend.repository;

import com.orderready.backend.entity.ProductStockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductStockTransactionRepository extends JpaRepository<ProductStockTransaction, Long> {
    List<ProductStockTransaction> findByProduct_IdOrderByCreatedAtDesc(Long productId);
    Optional<ProductStockTransaction> findFirstByProduct_IdOrderByCreatedAtDesc(Long productId);
    boolean existsByOrder_Id(Long orderId);
}