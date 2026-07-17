package com.orderready.backend.entity;

import jakarta.persistence.*;

// bir tedarikçinin hangi malzemeyi sağladığını temsil eder (basit bağlantı, süre/stok bilgisi yok)
@Entity
@Table(name = "supplier_materials")
public class SupplierMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }
}
