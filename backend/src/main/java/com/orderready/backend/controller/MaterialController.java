package com.orderready.backend.controller;

import com.orderready.backend.entity.Material;
import com.orderready.backend.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Material yönetimi için REST endpoint'lerini sunar
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/materials")
public class MaterialController {

    @Autowired
    private MaterialRepository materialRepository;

    // GET /api/materials - tüm malzemeleri döndürür
    @GetMapping
    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    // POST /api/materials - yeni malzeme oluşturur
    @PostMapping
    public Material createMaterial(@RequestBody Material material) {
        return materialRepository.save(material);
    }
}