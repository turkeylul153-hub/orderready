package com.orderready.backend.controller;

import com.orderready.backend.entity.Order;
import com.orderready.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    // GET /api/orders - tüm siparişleri döndürür (üretim planlama ekranı için)
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
