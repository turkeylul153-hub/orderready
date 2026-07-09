package com.orderready.backend.controller;

import com.orderready.backend.entity.Product;
import com.orderready.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//bu sınıf webden gelen product işlemlerine ait http isteklerini karşılar.
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // frontend tarafından get isteği geldiğinde bu kod çalışır.
    @GetMapping
    public List<Product> getAllProducts() {
        // findall fonk. tetiklenir veri tabanına giderek ürünleri listeler ve kullanıcıya geri döner
        return productRepository.findAll();
    }
}
