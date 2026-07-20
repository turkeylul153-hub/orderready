package com.orderready.backend.controller;

import com.orderready.backend.entity.SupplierMaterial;
import com.orderready.backend.repository.SupplierMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/supplier-materials")
public class SupplierMaterialController {

    @Autowired
    private SupplierMaterialRepository supplierMaterialRepository;

    @GetMapping
    public List<SupplierMaterial> getAllLinks() {
        return supplierMaterialRepository.findAll();
    }

    // POST /api/supplier-materials - bir tedarikçiyi bir malzemeyle ilişkilendirir
    @PostMapping
    public SupplierMaterial createLink(@RequestBody SupplierMaterial link) {
        return supplierMaterialRepository.save(link);
    }
}