package com.orderready.backend.controller;

import com.orderready.backend.entity.*;
import com.orderready.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/product-stock")
public class ProductStockController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductStockTransactionRepository transactionRepository;

    // GET /api/product-stock - her ürünün güncel (bitmiş) stoğunu listeler
    @GetMapping
    public List<ProductStockTransaction> getCurrentStock() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> transactionRepository
                        .findFirstByProduct_IdOrderByCreatedAtDesc(product.getId())
                        .orElse(null))
                .filter(tx -> tx != null)
                .toList();
    }
}
