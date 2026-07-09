package com.orderready.backend.repository;

import com.orderready.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
// temel veri tabanı işlemleri ( findAll, save, findByid...)
public interface ProductRepository extends JpaRepository<Product, Long> {
}