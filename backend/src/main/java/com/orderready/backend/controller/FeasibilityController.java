package com.orderready.backend.controller;

import com.orderready.backend.dto.FeasibilityRequest;
import com.orderready.backend.dto.FeasibilityResponse;
import com.orderready.backend.service.FeasibilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// sipariş uygunluk kontrolü için REST endpoint'ini sunar
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/feasibility")
public class FeasibilityController {

    @Autowired
    private FeasibilityService feasibilityService;

    // POST /api/feasibility/check - ürün ve miktar için uygunluk kontrolü yapar
    @PostMapping("/check")
    public FeasibilityResponse checkFeasibility(@RequestBody FeasibilityRequest request) {
        return feasibilityService.checkFeasibility(request);
    }
}
