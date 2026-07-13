package com.orderready.backend.repository;

import com.orderready.backend.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// recipe için temel veritabanı işlemlerini sağlar
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    // belirli bir ürüne ait tüm reçete satırlarını getirir
    List<Recipe> findByProduct_Id(Long productId);
}