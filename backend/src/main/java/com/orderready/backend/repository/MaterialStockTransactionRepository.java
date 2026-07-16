package com.orderready.backend.repository;

import com.orderready.backend.entity.MaterialStockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaterialStockTransactionRepository extends JpaRepository<MaterialStockTransaction, Long> {
    // belirli bir malzemenin tüm hareketlerini, en yeniden en eskiye sıralı getirir
    List<MaterialStockTransaction> findByMaterial_IdOrderByCreatedAtDesc(Long materialId);

    // belirli bir malzemenin en son hareketini getirir - "şu an ne kadar var" sorusu için
    Optional<MaterialStockTransaction> findFirstByMaterial_IdOrderByCreatedAtDesc(Long materialId);
}
