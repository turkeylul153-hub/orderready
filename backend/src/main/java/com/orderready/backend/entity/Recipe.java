package com.orderready.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

// bir ürünün reçetesindeki tek bir malzeme satırı
@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    // 100 kg ürün başına kullanılan malzeme miktarı
    @Column(name = "quantity_per_100kg", nullable = false)
    private BigDecimal quantityPer100kg;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }

    public BigDecimal getQuantityPer100kg() { return quantityPer100kg; }
    public void setQuantityPer100kg(BigDecimal quantityPer100kg) { this.quantityPer100kg = quantityPer100kg; }
}