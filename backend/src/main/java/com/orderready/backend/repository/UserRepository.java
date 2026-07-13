package com.orderready.backend.repository;

import com.orderready.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// user için temel veritabanı işlemlerini sağlar
public interface UserRepository extends JpaRepository<User, Long> {
    // kullanıcı adına göre kullanıcıyı bulur (giriş yaparken kullanılacak)
    Optional<User> findByUsername(String username);
}
