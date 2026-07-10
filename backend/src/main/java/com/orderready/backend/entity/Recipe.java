package com.orderready.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

// bir ürünün reçetesindeki tek bir malzeme satırı
// Örnek: "Yüksek Yoğunluklu Orijinal Poşet, 10 kg için 8.5 kg HDPE kullanır"
@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // bu reçete satırının hangi ürüne ait olduğu (Many-to-One: birden çok reçete satırı, bir ürüne bağlı)
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // bu reçete satırının hangi malzemeyi kullandığı (Many-to-One: birden çok reçete satırı, bir malzemeye bağlı)
    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    // 10 kg ürün başına kullanılan malzeme miktarı
    @Column(name = "quantity_per_10kg", nullable = false)
    private BigDecimal quantityPer10kg;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }

    public BigDecimal getQuantityPer10kg() { return quantityPer10kg; }
    public void setQuantityPer10kg(BigDecimal quantityPer10kg) { this.quantityPer10kg = quantityPer10kg; }
}