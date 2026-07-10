package com.orderready.backend.repository;

import com.orderready.backend.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

// Recipe için temel veritabanı işlemlerini sağlar
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}