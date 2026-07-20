package com.orderready.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

// hammadde veya ambalaj malzemesini temsil eder
@Entity
@Table(name = "materials")
public class Material {

    private String description;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // RAW_MATERIAL veya PACKAGING
    @Column(nullable = false)
    private String type;

    // ölçü birimi (kg, adet vb.)
    @Column(nullable = false)
    private String unit;

    // bu seviyenin altına düşünce depo çalışanına uyarı gösterilecek
    @Column(name = "low_stock_threshold")
    private BigDecimal lowStockThreshold;

    public String getDescription()  {return description; }
    public void setDescription( String description) {this.description = description;}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(BigDecimal lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }
}
