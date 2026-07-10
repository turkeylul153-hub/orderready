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

    //tüm tedarik süresi kayıtlarını döndürür
    @GetMapping
    public List<SupplierMaterial> getAllSupplierMaterials() {
        return supplierMaterialRepository.findAll();
    }
}
