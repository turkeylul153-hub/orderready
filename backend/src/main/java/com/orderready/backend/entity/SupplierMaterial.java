package com.orderready.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

// bir tedarikçinin, bir malzemeyi ne hızda (lead time) tedarik ettiğini temsil eder
@Entity
@Table(name = "supplier_materials")
public class SupplierMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // bu tedarik bilgisinin hangi firmaya ait olduğu
    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    // bu tedarik bilgisinin hangi malzemeye ait olduğu
    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    // tedarik süresinin hesaplandığı miktar birimi (örn: 10 kg)
    @Column(name = "lead_time_batch_size", nullable = false)
    private BigDecimal leadTimeBatchSize;

    // o miktarın kaç günde tedarik edildiği (örn: 2 gün) - tedarikçide hazır stok yoksa kullanılan tahmini süre
    @Column(name = "lead_time_days", nullable = false)
    private BigDecimal leadTimeDays;

    // tedarikçinin şu an elinde hazır bulunan miktar - tedarikçi kendi panelinden günceller
    @Column(name = "available_quantity", nullable = false)
    private BigDecimal availableQuantity;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }

    public BigDecimal getLeadTimeBatchSize() { return leadTimeBatchSize; }
    public void setLeadTimeBatchSize(BigDecimal leadTimeBatchSize) { this.leadTimeBatchSize = leadTimeBatchSize; }

    public BigDecimal getLeadTimeDays() { return leadTimeDays; }
    public void setLeadTimeDays(BigDecimal leadTimeDays) { this.leadTimeDays = leadTimeDays; }

    public BigDecimal getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(BigDecimal availableQuantity) { this.availableQuantity = availableQuantity; }
}
