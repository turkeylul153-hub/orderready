package com.orderready.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// bir ürünü (poşet türünü) ve üretim özelliklerini temsil eder
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // saatte kaç kg üretildiği
    @Column(name = "production_rate_kg_per_hour")
    private BigDecimal productionRateKgPerHour;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getProductionRateKgPerHour() { return productionRateKgPerHour; }
    public void setProductionRateKgPerHour(BigDecimal productionRateKgPerHour) { this.productionRateKgPerHour = productionRateKgPerHour; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
