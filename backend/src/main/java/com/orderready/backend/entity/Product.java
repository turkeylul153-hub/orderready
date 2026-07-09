package com.orderready.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
// bir üretim partisinde kaç kg üretildi?
    @Column(name = "production_batch_size")
    private BigDecimal productionBatchSize;
// üretim kaç gün sürüyor?
    @Column(name = "production_time_days")
    private BigDecimal productionTimeDays;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Getter, Setter priv degiskene kontrollu erisim ve degistirme
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getProductionBatchSize() { return productionBatchSize; }
    public void setProductionBatchSize(BigDecimal productionBatchSize) { this.productionBatchSize = productionBatchSize; }

    public BigDecimal getProductionTimeDays() { return productionTimeDays; }
    public void setProductionTimeDays(BigDecimal productionTimeDays) { this.productionTimeDays = productionTimeDays; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}