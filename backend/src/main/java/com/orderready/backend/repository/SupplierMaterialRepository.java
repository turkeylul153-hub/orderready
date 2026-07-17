package com.orderready.backend.repository;

import com.orderready.backend.entity.SupplierMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierMaterialRepository extends JpaRepository<SupplierMaterial, Long> {
    // Bir malzemeyi sağlayan tedarikçi(leri) bulur
    List<SupplierMaterial> findByMaterial_Id(Long materialId);
}
