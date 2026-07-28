package com.orderready.backend.controller;

import com.orderready.backend.entity.User;
import com.orderready.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // GET /api/users - tüm kullanıcıları listeler (yönetim ekranı için)
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // PUT /api/users/{id}/role - kullanıcının rolünü günceller
    @PutMapping("/{id}/role")
    public User updateRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        user.setRole(request.getRole());
        return userRepository.save(user);
    }

    // İç içe basit bir DTO - sadece yeni rol bilgisini taşır
    public static class RoleUpdateRequest {
        private String role;

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}