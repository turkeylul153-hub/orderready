package com.orderready.backend.controller;

import com.orderready.backend.dto.LoginRequest;
import com.orderready.backend.dto.LoginResponse;
import com.orderready.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public LoginResponse getCurrentUser(@RequestParam String token) {
        return authService.getUserByToken(token);
    }
}
