package com.orderready.backend.entity;

import jakarta.persistence.*;

// sisteme giriş yapan kullanıcıyı temsil eder (depo çalışanı, planlamacı, veya tedarikçi)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    // şifrenin hash'lenmiş hali saklanır, asla düz metin değil
    @Column(nullable = false)
    private String password;

    // WAREHOUSE (depo), PLANNER (üretim planlama), veya SUPPLIER (tedarikçi)
    @Column(nullable = false)
    private String role;

    // sadece SUPPLIER rolü için doldurulur - bu kullanıcı hangi firmaya ait
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
}
