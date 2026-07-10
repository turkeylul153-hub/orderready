package com.orderready.backend.controller;

import com.orderready.backend.entity.Material;
import com.orderready.backend.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Material yönetimi için REST endpoint'lerini sunar
@RestController
@RequestMapping("/api/materials") // url belirler
public class MaterialController {

    @Autowired
    private MaterialRepository materialRepository;

    // GET /api/materials - tüm malzemeleri döndürür
    @GetMapping
    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }
}