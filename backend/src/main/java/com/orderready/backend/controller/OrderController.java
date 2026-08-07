package com.orderready.backend.controller;

import com.orderready.backend.dto.CancelRequest;
import com.orderready.backend.dto.PinRequest;
import com.orderready.backend.entity.Order;
import com.orderready.backend.entity.ProductStockTransaction;
import com.orderready.backend.entity.Warehouse;
import com.orderready.backend.repository.MaterialStockTransactionRepository;
import com.orderready.backend.repository.OrderRepository;
import com.orderready.backend.repository.ProductStockTransactionRepository;
import com.orderready.backend.repository.WarehouseRepository;
import com.orderready.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private MaterialStockTransactionRepository materialTxRepository;

    @Autowired
    private ProductStockTransactionRepository productTxRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private WarehouseRepository warehouseRepository;

    // GET /api/orders - siparişleri sayfalı olarak döndürür, isteğe bağlı durum filtresiyle
    @GetMapping
    public Page<Order> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        if (status != null) {
            return orderRepository.findByStatus(status, pageable);
        }
        return orderRepository.findAll(pageable);
    }

    // POST /api/orders - yeni sipariş oluşturur; depoda gerçekten müsait ürün varsa otomatik tamamlar
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        order.setStatus("PENDING");
        Order savedOrder = orderRepository.save(order);

        BigDecimal totalProductStock = productTxRepository
                .findFirstByProduct_IdOrderByCreatedAtDesc(savedOrder.getProduct().getId())
                .map(ProductStockTransaction::getBalanceAfter)
                .orElse(BigDecimal.ZERO);

        // COMPLETED durumda, henüz sevk edilmemiş siparişlerin toplamını bul (bunlar zaten "söz verilmiş")
        List<Order> reservedOrders = orderRepository.findByProduct_IdAndStatus(savedOrder.getProduct().getId(), "COMPLETED");
        BigDecimal reservedQuantity = reservedOrders.stream()
                .map(Order::getQuantityKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Gerçekten müsait olan stok = toplam - zaten söz verilmiş olan
        BigDecimal availableProductStock = totalProductStock.subtract(reservedQuantity);

        boolean fullyCoveredByStock = availableProductStock.compareTo(savedOrder.getQuantityKg()) >= 0;

        if (fullyCoveredByStock) {
            Warehouse warehouse = warehouseRepository.findAll().get(0);
            BigDecimal newBalance = totalProductStock.subtract(savedOrder.getQuantityKg());

            ProductStockTransaction tx = new ProductStockTransaction();
            tx.setProduct(savedOrder.getProduct());
            tx.setWarehouse(warehouse);
            tx.setQuantityChange(savedOrder.getQuantityKg().negate());
            tx.setBalanceAfter(newBalance);
            tx.setType("ORDER_CONSUMPTION");
            tx.setOrder(savedOrder);
            tx.setCreatedAt(LocalDateTime.now());
            productTxRepository.save(tx);

            savedOrder.setStatus("COMPLETED");
            savedOrder.setShortfallQuantityKg(BigDecimal.ZERO);
            return orderRepository.save(savedOrder);
        }

        return savedOrder;
    }

    // DELETE /api/orders/{id} - sadece PENDING durumundaki siparişleri siler
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("Sadece beklemede olan siparişler silinebilir");
        }

        boolean hasHistory = materialTxRepository.existsByOrder_Id(id) || productTxRepository.existsByOrder_Id(id);
        if (hasHistory) {
            throw new RuntimeException("Bu siparişin işlem geçmişi var, silinemez");
        }

        orderRepository.deleteById(id);
    }

    // PUT /api/orders/{id}/cancel - siparişi iptal eder (silmez, durumu değiştirir)
    @PutMapping("/{id}/cancel")
    public Order cancelOrder(@PathVariable Long id, @RequestBody CancelRequest request) {
        return orderService.cancelOrder(id, request.getPin(), request.getReason());
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