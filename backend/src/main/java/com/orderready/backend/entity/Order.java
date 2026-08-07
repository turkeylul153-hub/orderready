package com.orderready.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

//müşteri siparişini temsil eder
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity_kg", nullable = false)
    private BigDecimal quantityKg;
    private BigDecimal shortfallQuantityKg;

    public BigDecimal getShortfallQuantityKg() { return shortfallQuantityKg; }
    public void setShortfallQuantityKg(BigDecimal shortfallQuantityKg) { this.shortfallQuantityKg = shortfallQuantityKg; }

    @Column(name = "customer_name")
    private String customerName;

    // URGENT (acil) veya NORMAL
    @Column(nullable = false)
    private String priority = "NORMAL";

    // PENDING (beklemede), IN_PRODUCTION (üretimde), COMPLETED (tamamlandı), SHIPPED (gönderildi)
    @Column(nullable = false)
    private String status = "PENDING";

    // müşterinin özel isteği veya sipariş notu
    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public BigDecimal getQuantityKg() { return quantityKg; }
    public void setQuantityKg(BigDecimal quantityKg) { this.quantityKg = quantityKg; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
