package com.orderready.backend.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id no otomatik artar.
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // RAW_MATERIAL (hammadde) veya PACKAGING (ambalaj)
    @Column(nullable = false)
    private String type;

    // ölçü birimi (kg, adet vb.)
    @Column(nullable = false)
    private String unit;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}