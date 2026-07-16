package com.orderready.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// bir hammaddenin stok hareketini (ekleme/çıkarma) temsil eder - hiç silinmez veya değiştirilmez
@Entity
@Table(name = "material_stock_tx")
public class MaterialStockTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    // bu hareketle değişen miktar (pozitif: ekleme, negatif: çıkarma)
    @Column(name = "quantity_change", nullable = false)
    private BigDecimal quantityChange;

    // bu hareketten sonraki toplam bakiye
    @Column(name = "balance_after", nullable = false)
    private BigDecimal balanceAfter;

    // INITIAL, ADDITION, REMOVAL, ADJUSTMENT, ORDER_CONSUMPTION
    @Column(nullable = false)
    private String type;

    // eğer bu hareket bir siparişten kaynaklandıysa, hangi sipariş (elle girilen hareketlerde boş kalır)
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "performed_by")
    private User performedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }

    public Warehouse getWarehouse() { return warehouse; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }

    public BigDecimal getQuantityChange() { return quantityChange; }
    public void setQuantityChange(BigDecimal quantityChange) { this.quantityChange = quantityChange; }

    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public User getPerformedBy() { return performedBy; }
    public void setPerformedBy(User performedBy) { this.performedBy = performedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}