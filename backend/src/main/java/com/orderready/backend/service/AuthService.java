package com.orderready.backend.service;

import com.orderready.backend.dto.LoginRequest;
import com.orderready.backend.dto.LoginResponse;
import com.orderready.backend.entity.User;
import com.orderready.backend.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// kullanıcı giriş (login) işlemlerini yönetir
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {
        // kullanıcıyı adına göre bul
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı adı veya şifre hatalı"));

        // girilen şifreyi, veritabanındaki hash'lenmiş şifreyle karşılaştır
        boolean passwordMatches = BCrypt.checkpw(request.getPassword(), user.getPassword());

        if (!passwordMatches) {
            throw new RuntimeException("Kullanıcı adı veya şifre hatalı");
        }

        // giriş başarılı, cevabı hazırla
        LoginResponse response = new LoginResponse();
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        if (user.getSupplier() != null) {
            response.setSupplierId(user.getSupplier().getId());
        }
        return response;
    }
}
