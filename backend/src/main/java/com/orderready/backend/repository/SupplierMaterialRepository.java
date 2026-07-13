package com.orderready.backend.repository;

import com.orderready.backend.entity.SupplierMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// SupplierMaterial için temel veritabanı işlemlerini sağlar
public interface SupplierMaterialRepository extends JpaRepository<SupplierMaterial, Long> {
    // belirli bir malzemeyi sağlayan tüm tedarikçi kayıtlarını getirir
    List<SupplierMaterial> findByMaterial_Id(Long materialId);
}