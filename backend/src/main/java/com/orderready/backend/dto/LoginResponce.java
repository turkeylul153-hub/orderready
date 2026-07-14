package com.orderready.backend.dto;

// giriş başarılı olduğunda dönen bilgiler
public class LoginResponse {
    private String username;
    private String role;
    private Long supplierId;  // sadece SUPPLIER rolü için dolu olur

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
}