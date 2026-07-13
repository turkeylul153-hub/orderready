package com.orderready.backend.controller;

import com.orderready.backend.dto.OrderCalculationRequest;
import com.orderready.backend.dto.OrderCalculationResponse;
import com.orderready.backend.service.OrderCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// üretim planlama hesaplaması için REST endpoint'ini sunar
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/calculate")
public class OrderCalculationController {

    @Autowired
    private OrderCalculationService orderCalculationService;

    // POST /api/calculate - ürün ve miktar bilgisiyle hesaplama yapar
    @PostMapping
    public OrderCalculationResponse calculate(@RequestBody OrderCalculationRequest request) {
        return orderCalculationService.calculate(request);
    }
}
// hesaplama yapılmasını istediğimiz için postmapping kullanıyoruz getmapping değil
