package com.orderready.backend.repository;

import com.orderready.backend.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

// Material için temel veritabanı işlemlerini sağlar
public interface MaterialRepository extends JpaRepository<Material, Long> {
}