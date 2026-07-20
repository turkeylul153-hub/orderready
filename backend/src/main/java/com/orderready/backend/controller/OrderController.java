package com.orderready.backend.controller;

import com.orderready.backend.entity.Order;
import com.orderready.backend.repository.OrderRepository;
import com.orderready.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.orderready.backend.dto.PinRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    // GET /api/orders - tüm siparişleri döndürür (üretim planlama ekranı için)
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // POST /api/orders - yeni sipariş oluşturur
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderRepository.save(order);
    }

    // PUT /api/orders/{id}/start-production - üretime başlatır, hammaddeyi düşer
    @PutMapping("/{id}/start-production")
    public Order startProduction(@PathVariable Long id) {
        return orderService.startProduction(id);
    }

    // PUT /api/orders/{id}/complete - üretimi tamamlar, ürün stoğuna ekler
    @PutMapping("/{id}/complete")
    public Order completeOrder(@PathVariable Long id) {
        return orderService.completeOrder(id);
    }

    // PUT /api/orders/{id}/ship - gönderimi kaydeder, ürün stoğundan düşer
    @PutMapping("/{id}/ship")
    public Order shipOrder(@PathVariable Long id) {
        return orderService.shipOrder(id);
    }
    // PUT /api/orders/{id}/revert-production - üretimi geri alır, hammaddeyi geri ekler
    @PutMapping("/{id}/revert-production")
    public Order revertProduction(@PathVariable Long id, @RequestBody PinRequest request) {
        return orderService.revertProduction(id, request.getPin());
    }

    // PUT /api/orders/{id}/revert-shipment - sevkiyatı geri alır, ürünü depoya geri ekler
    @PutMapping("/{id}/revert-shipment")
    public Order revertShipment(@PathVariable Long id, @RequestBody PinRequest request) {
        return orderService.revertShipment(id, request.getPin());
    }
}
